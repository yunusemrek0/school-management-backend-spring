package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.ChooseLessonProgramRequest;
import com.project.schoolmanagment.payload.request.user.StudentRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.StudentResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.business.LessonProgramService;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final PasswordEncoder passwordEncoder;
    private final LessonProgramService lessonProgramService;
    private final DateTimeValidator dateTimeValidator;
    private final MethodHelper methodHelper;
    private final UserMapper userMapper;

    public ResponseMessage<StudentResponse> saveStudent(StudentRequest studentRequest) {

        User advisorTeacher = methodHelper.isUserExist(studentRequest.getAdvisorTeacherId());
        methodHelper.checkIsAdvisor(advisorTeacher);

        uniquePropertyValidator.checkDuplicate(
                studentRequest.getUsername(),
                studentRequest.getSsn(),
                studentRequest.getPhoneNumber(),
                studentRequest.getEmail());

        User student = userMapper.mapUserRequestToUser(studentRequest);
        //set missing properties
        student.setAdvisorTeacherId(advisorTeacher.getId());
        student.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
        student.setActive(true);
        student.setIsAdvisor(false);
        student.setStudentNumber(getLastNumber());

        User savedStudent = userRepository.save(student);

        return ResponseMessage.<StudentResponse>builder()
                .message(SuccessMessages.STUDENT_SAVE)
                .returnBody(userMapper.mapUserToStudentResponse(savedStudent))
                .build();


    }

    private int getLastNumber(){
        if (!userRepository.findStudent(RoleType.STUDENT)){
            return 1000;
        }
        return userRepository.getMaxStudentNumber()+1;
    }

    public ResponseMessage<StudentResponse> addLessonProgram(HttpServletRequest httpServletRequest,
                                                             ChooseLessonProgramRequest lessonProgramRequest) {

        String username = (String) httpServletRequest.getAttribute("username");
        User loggedInStudent = methodHelper.loadUserByName(username);

        Set<LessonProgram> lessonPrograms = lessonProgramService.getLessonProgramById(lessonProgramRequest.getLessonProgramId());
        Set<LessonProgram> existingLessonProgram = loggedInStudent.getLessonProgramList();

        dateTimeValidator.checkLessonPrograms(existingLessonProgram,lessonPrograms);

        existingLessonProgram.addAll(lessonPrograms);
        loggedInStudent.setLessonProgramList(existingLessonProgram);

        User savedStudent = userRepository.save(loggedInStudent);

        return ResponseMessage.<StudentResponse>builder()
                .message(SuccessMessages.LESSON_PROGRAM_ADD_TO_STUDENT)
                .returnBody(userMapper.mapUserToStudentResponse(savedStudent))
                .httpStatus(HttpStatus.CREATED)
                .build();




    }
}
