package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.concretes.businnes.StudentInfo;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.Note;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.ConflictException;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.StudentInfoMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.StudentInfoRequest;
import com.project.schoolmanagment.payload.request.business.StudentInfoUpdateRequest;
import com.project.schoolmanagment.payload.response.businnes.LessonResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.businnes.StudentInfoResponse;
import com.project.schoolmanagment.repository.business.StudentInfoRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentInfoRepository studentInfoRepository;
    private final MethodHelper methodHelper;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;
    private final StudentInfoMapper studentInfoMapper;
    private final PageableHelper pageableHelper;

    @Value("${midterm.exam.impact.percentage}")
    private Double midtermExamPercentage;
    @Value("${final.exam.impact.percentage}")
    private Double finalExamPercentage;

    public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest servletRequest, StudentInfoRequest studentInfoRequest) {

        String username = (String) servletRequest.getAttribute("username");
        User teacher = methodHelper.loadUserByName(username);

        User student = methodHelper.isUserExist(studentInfoRequest.getStudentId());
        methodHelper.checkRole(student, RoleType.STUDENT);

        Lesson lesson = lessonService.isLessonExistsById(studentInfoRequest.getLessonId());

        EducationTerm educationTerm = educationTermService.isEducationTermExists(studentInfoRequest.getEducationTermId());

        validateLessonDuplication(student.getId(),lesson.getLessonName());

        Note note = checkLetterGrade(calculateExamAverage(studentInfoRequest.getMidtermExam(),studentInfoRequest.getFinalExam()));

        StudentInfo studentInfo = studentInfoMapper.mapStudentInfoRequestToStudentInfo(
                studentInfoRequest
                ,note
                ,calculateExamAverage(studentInfoRequest.getMidtermExam(),studentInfoRequest.getFinalExam()));

        studentInfo.setStudent(student);
        studentInfo.setEducationTerm(educationTerm);
        studentInfo.setTeacher(teacher);
        studentInfo.setLesson(lesson);

        StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);

        return ResponseMessage.<StudentInfoResponse> builder()
                .message(SuccessMessages.STUDENT_INFO_SAVED)
                .returnBody(studentInfoMapper.mapStudentInfoToStudentInfoResponse(savedStudentInfo))
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    private void validateLessonDuplication(Long studentId,String lessonName){
        boolean isLessonDuplicationExists = studentInfoRepository.giveMeDuplications(lessonName,studentId);

        if (isLessonDuplicationExists){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_LESSON_MESSAGE,lessonName));
        }
    }

    private Double calculateExamAverage(Double midtermExam,Double finalExam){

        return (midtermExam*midtermExamPercentage) + (finalExam*finalExamPercentage);
    }
    private Note checkLetterGrade(Double average){

        if(average<50.0) {
            return Note.FF;
        } else if (average<60) {
            return Note.DD;
        } else if (average<65) {
            return Note.CC;
        } else if (average<70) {
            return  Note.CB;
        } else if (average<75) {
            return  Note.BB;
        } else if (average<80) {
            return Note.BA;
        } else {
            return Note.AA;
        }
    }


    public ResponseMessage<StudentInfoResponse> updateStudentInfo(Long id, StudentInfoUpdateRequest studentInfoRequest) {

        StudentInfo studentInfo =isStudentInfoExistsWithId(id);
        Lesson lesson = lessonService.isLessonExistsById(studentInfoRequest.getLessonId());
        EducationTerm educationTerm = educationTermService.isEducationTermExists(studentInfoRequest.getEducationTermId());
        Double noteAverage = calculateExamAverage(studentInfoRequest.getMidtermExam(),studentInfoRequest.getFinalExam());
        Note note = checkLetterGrade(noteAverage);


        StudentInfo studentInfoToUpdate = studentInfoMapper.mapStudentInfoUpdateRequestToStudentInfo(
                studentInfoRequest, id, lesson, educationTerm, note, noteAverage);

        studentInfoToUpdate.setStudent(studentInfo.getStudent());
        studentInfoToUpdate.setTeacher(studentInfo.getTeacher());

        StudentInfo updatedStudentInfo = studentInfoRepository.save(studentInfoToUpdate);

        return ResponseMessage.<StudentInfoResponse> builder()
                .message(SuccessMessages.STUDENT_INFO_UPDATE)
                .returnBody(studentInfoMapper.mapStudentInfoToStudentInfoResponse(updatedStudentInfo))
                .httpStatus(HttpStatus.OK)
                .build();






    }

    private StudentInfo isStudentInfoExistsWithId(Long id){
        return studentInfoRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException(String.format(ErrorMessages.STUDENT_INFO_NOT_FOUND_BY_STUDENT_ID,id))
        );
    }

    public ResponseMessage deleteById(Long id) {
        isStudentInfoExistsWithId(id);
        studentInfoRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.STUDENT_INFO_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();
    }


    public Page<StudentInfoResponse> getLessonByPage(int page, int size, String sort, String type) {

        Pageable pageable = pageableHelper.getPageableWithProperties(page,size,sort,type);

        return studentInfoRepository.findAll(pageable)
                .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
    }

    public ResponseMessage<StudentInfoResponse> getById(Long id) {
        return ResponseMessage.<StudentInfoResponse> builder()
                .message(SuccessMessages.STUDENT_INFO_FOUND)
                .returnBody(studentInfoMapper.mapStudentInfoToStudentInfoResponse(isStudentInfoExistsWithId(id)))
                .httpStatus(HttpStatus.OK)
                .build();
    }


    public List<StudentInfoResponse> getByStudentId(Long id) {
        User student = methodHelper.isUserExist(id);
        methodHelper.checkRole(student,RoleType.STUDENT);
/*
        return student.getStudentInfos()
                .stream()
                .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse)
                .collect(Collectors.toList());

 */
        return studentInfoRepository.findByStudentInfo(id)
                .stream()
                .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse)
                .collect(Collectors.toList());



    }
}
