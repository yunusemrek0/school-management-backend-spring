package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.payload.mappers.LessonProgramMapper;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.AddLessonProgramToTeacherRequest;
import com.project.schoolmanagment.payload.request.user.TeacherRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.StudentResponse;
import com.project.schoolmanagment.payload.response.user.TeacherResponse;
import com.project.schoolmanagment.payload.response.user.UserResponse;
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
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {


    private final UserRepository userRepository;

    private final UserRoleService userRoleService;

    private final UniquePropertyValidator uniquePropertyValidator;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final LessonProgramService lessonProgramService;

    private final MethodHelper methodHelper;

    private final DateTimeValidator dateTimeValidator;

    private final LessonProgramMapper lessonProgramMapper;



    @Transactional
    public ResponseMessage<UserResponse> saveTeacher(TeacherRequest teacherRequest) {
        Set<LessonProgram> lessonPrograms = lessonProgramService.getLessonProgramById(teacherRequest.getLessonProgramIdList());

        uniquePropertyValidator.checkDuplicate(
                teacherRequest.getUsername(),
                teacherRequest.getSsn(),
                teacherRequest.getPhoneNumber(),
                teacherRequest.getEmail());


        User teacher = userMapper.mapUserRequestToUser(teacherRequest);
        // set extra props that exist in teacher
        teacher.setIsAdvisor(teacherRequest.getIsAdvisorTeacher());
        teacher.setLessonProgramList(lessonPrograms);
        teacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));


        User savedTeacher = userRepository.save(teacher);

        return ResponseMessage.<UserResponse> builder()
                .message(SuccessMessages.TEACHER_SAVE)
                .httpStatus(HttpStatus.CREATED)
                .returnBody(userMapper.mapUserToUserResponse(savedTeacher))
                .build();
    }

    public ResponseMessage<UserResponse> changeAdvisorTeacherStatus(Long id) {

        User teacher = methodHelper.isUserExist(id);
        methodHelper.checkRole(teacher,RoleType.TEACHER);
        methodHelper.checkIsAdvisor(teacher);

        teacher.setIsAdvisor(false);
        userRepository.save(teacher);

        List<User> allStudents = userRepository.findByAdvisorTeacherId(id);

        if (!allStudents.isEmpty()){
            allStudents.forEach(students -> students.setAdvisorTeacherId(null));
        }

        return ResponseMessage.<UserResponse> builder()
                .message(SuccessMessages.ADVISOR_TEACHER_DELETE)
                .returnBody(userMapper.mapUserToUserResponse(teacher))
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public List<UserResponse> getAllAdvisorTeacher() {

        return userRepository
                .findAll()
                .stream()
                .filter(teacher -> teacher.getIsAdvisor().equals(true))
                .map(userMapper::mapUserToUserResponse)
                .collect(Collectors.toList());

        // check there null point ex for id 35 user user.getUserRole()
    }

    public ResponseMessage<UserResponse> updateTeacherByManagers(TeacherRequest teacherRequest, Long id) {

        User teacher = methodHelper.isUserExist(id);
        methodHelper.checkRole(teacher,RoleType.TEACHER);

        Set<LessonProgram> lessonPrograms =  lessonProgramService.getLessonProgramById(teacherRequest.getLessonProgramIdList());
        User teacherToUpdate = userMapper.mapUserRequestToUser(teacherRequest);
        teacherToUpdate.setId(teacher.getId());
        teacherToUpdate.setLessonProgramList(lessonPrograms);
        teacherToUpdate.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));

        User updatedTeacher = userRepository.save(teacherToUpdate);

        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.TEACHER_UPDATE)
                .returnBody(userMapper.mapUserToUserResponse(updatedTeacher))
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public List<StudentResponse> getAllStudentsByAdvisorTeacher(HttpServletRequest servletRequest) {

        String username = (String) servletRequest.getAttribute("username");
        User teacher = methodHelper.loadUserByName(username);

        methodHelper.checkIsAdvisor(teacher);

        return userRepository.findByAdvisorTeacherId(teacher.getId())
                .stream()
                .map(userMapper::mapUserToStudentResponse)
                .collect(Collectors.toList());



    }

    public ResponseMessage<UserResponse> addLessonProgramToTeacher(AddLessonProgramToTeacherRequest lessonProgramRequest) {

        User teacher = methodHelper.isUserExist(lessonProgramRequest.getTeacherId());
        methodHelper.checkRole(teacher,RoleType.TEACHER);

        Set<LessonProgram> existingLessonProgram = teacher.getLessonProgramList();
        Set<LessonProgram> requestLessonProgram = lessonProgramService.getLessonProgramById(lessonProgramRequest.getLessonProgramId());
        dateTimeValidator.checkLessonPrograms(existingLessonProgram,requestLessonProgram);

        existingLessonProgram.addAll(requestLessonProgram);
        teacher.setLessonProgramList(existingLessonProgram);

        User savedTeacher = userRepository.save(teacher);

        return ResponseMessage.<UserResponse> builder()
                .message(SuccessMessages.LESSON_PROGRAM_ADD_TO_TEACHER)
                .httpStatus(HttpStatus.OK)
                .returnBody(userMapper.mapUserToUserResponse(savedTeacher))
                .build();



    }
}
