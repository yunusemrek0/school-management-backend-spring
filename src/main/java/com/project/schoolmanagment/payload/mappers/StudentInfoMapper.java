package com.project.schoolmanagment.payload.mappers;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.concretes.businnes.StudentInfo;
import com.project.schoolmanagment.entity.enums.Note;
import com.project.schoolmanagment.payload.request.business.StudentInfoRequest;
import com.project.schoolmanagment.payload.request.business.StudentInfoUpdateRequest;
import com.project.schoolmanagment.payload.request.user.StudentRequest;
import com.project.schoolmanagment.payload.response.businnes.StudentInfoResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
public class StudentInfoMapper {

    private final UserMapper userMapper;
    public StudentInfo mapStudentInfoRequestToStudentInfo(StudentInfoRequest studentInfoRequest,
                                                          Note note, Double average){

        return StudentInfo.builder()
                .infoNote(studentInfoRequest.getInfoNote())
                .absentee(studentInfoRequest.getAbsentee())
                .midtermExam(studentInfoRequest.getMidtermExam())
                .finalExam(studentInfoRequest.getFinalExam())
                .examAverage(average)
                .letterGrade(note)
                .build();

    }

    public StudentInfoResponse mapStudentInfoToStudentInfoResponse(StudentInfo studentInfo){

        return StudentInfoResponse.builder()
                .id(studentInfo.getId())
                .infoNote(studentInfo.getInfoNote())
                .lessonName(studentInfo.getLesson().getLessonName())
                .creditScore(studentInfo.getLesson().getCreditScore())
                .isCompulsory(studentInfo.getLesson().getIsCompulsory())
                .educationTerm(studentInfo.getEducationTerm().getTerm())
                .absentee(studentInfo.getAbsentee())
                .midtermExam(studentInfo.getMidtermExam())
                .finalExam(studentInfo.getFinalExam())
                .note(studentInfo.getLetterGrade())
                .average(studentInfo.getExamAverage())
                .studentResponse(userMapper.mapUserToStudentResponse(studentInfo.getStudent()))
                .build();

    }


    public StudentInfo mapStudentInfoUpdateRequestToStudentInfo(StudentInfoUpdateRequest studentInfoUpdateRequest,
                                                                Long studentInfoId,
                                                                Lesson lesson,
                                                                EducationTerm educationTerm,
                                                                Note note,
                                                                Double average){


        return StudentInfo.builder()
                .id(studentInfoId)
                .infoNote(studentInfoUpdateRequest.getInfoNote())
                .midtermExam(studentInfoUpdateRequest.getMidtermExam())
                .finalExam(studentInfoUpdateRequest.getFinalExam())
                .absentee(studentInfoUpdateRequest.getAbsentee())
                .lesson(lesson)
                .educationTerm(educationTerm)
                .letterGrade(note)
                .examAverage(average)
                .build();

    }
}
