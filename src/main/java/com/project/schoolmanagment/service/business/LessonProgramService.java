package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.BadRequestException;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.LessonProgramMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.LessonProgramRequest;
import com.project.schoolmanagment.payload.response.businnes.LessonProgramResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.repository.business.LessonProgramRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.helper.PageableHelper;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

    private final LessonProgramRepository lessonProgramRepository;

    private final LessonService lessonService;

    private final EducationTermService educationTermService;

    private final DateTimeValidator dateTimeValidator;

    private final LessonProgramMapper lessonProgramMapper;

    private final PageableHelper pageableHelper;

    private final MethodHelper methodHelper;


    public ResponseMessage<LessonProgramResponse> saveLessonProgram(LessonProgramRequest lessonProgramRequest) {
        Set<Lesson> lessons = lessonService.getLessonByIdSet(lessonProgramRequest.getLessonIdList());

        EducationTerm educationTerm = educationTermService.isEducationTermExists(lessonProgramRequest.getEducationTermId());

        dateTimeValidator.checkTimeWithException(lessonProgramRequest.getStartTime(),
                lessonProgramRequest.getStopTime());

        LessonProgram lessonProgram = lessonProgramMapper.mapLessonProgramRequestToLessonProgram(
                lessonProgramRequest,lessons,educationTerm);

        LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);

        return ResponseMessage.<LessonProgramResponse> builder()
                .message(SuccessMessages.LESSON_PROGRAM_SAVE)
                .returnBody(lessonProgramMapper.mapLessonProgramToLessonProgramResponse(savedLessonProgram))
                .httpStatus(HttpStatus.CREATED)
                .build();


    }

    public List<LessonProgramResponse> getAll() {

        return lessonProgramRepository
                .findAll()
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toList());
    }

    public List<LessonProgramResponse> getAllUnassigned() {

        return lessonProgramRepository
                .findByUsers_IdNull()
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toList());
    }

    public List<LessonProgramResponse> getAllAssigned() {

        return lessonProgramRepository
                .findByUsers_IdNotNull()
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toList());
    }

    public LessonProgramResponse getById(Long id) {
        return lessonProgramMapper.mapLessonProgramToLessonProgramResponse(
                isLessonProgramExists(id));
    }

    private LessonProgram isLessonProgramExists(Long id){
        return lessonProgramRepository
                .findById(id).orElseThrow(
                        ()-> new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_PROGRAM_MESSAGE,id))
                );
    }

    public ResponseMessage deleteById(Long id) {
        isLessonProgramExists(id);
        lessonProgramRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.LESSON_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public Page<LessonProgramResponse> getLessonProgramByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page,size,sort,type);

        return lessonProgramRepository
                .findAll(pageable)
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse);
    }

    public Set<LessonProgram> getLessonProgramById(Set<Long> idSet){
        Set<LessonProgram> lessonPrograms = lessonProgramRepository.getLessonProgramByIdList(idSet);

        if (lessonPrograms.isEmpty()){
            throw new BadRequestException(ErrorMessages.NOT_FOUND_LESSON_PROGRAM_MESSAGE_WITHOUT_ID_INFO);
        }

        return lessonPrograms;
    }

    public Set<LessonProgramResponse> getAllLessonProgramsByUsername(HttpServletRequest httpServletRequest) {

        String username = (String) httpServletRequest.getAttribute("username");

        return lessonProgramRepository
                .getLessonProgramByUsername(username)
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toSet());
    }

    public Set<LessonProgramResponse> getAllLessonProgramsByTeacherId(Long id) {

        User teacher = methodHelper.isUserExist(id);
        //check if user is a teacher
        methodHelper.checkRole(teacher, RoleType.TEACHER);

        return lessonProgramRepository
                .findByUsers_IdEquals(id)
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toSet());




    }

    public Set<LessonProgramResponse> getAllLessonProgramsByStudentId(Long id) {

        User student = methodHelper.isUserExist(id);
        //check if user is a teacher
        methodHelper.checkRole(student, RoleType.STUDENT);

        return lessonProgramRepository
                .findByUsers_IdEquals(id)
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toSet());
    }
}
