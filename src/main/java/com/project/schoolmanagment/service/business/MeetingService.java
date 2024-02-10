package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.entity.concretes.businnes.Meet;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.BadRequestException;
import com.project.schoolmanagment.exception.ConflictException;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.MeetingMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.MeetingRequest;
import com.project.schoolmanagment.payload.response.businnes.MeetingResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.repository.business.MeetingRepository;
import com.project.schoolmanagment.service.helper.MeetingHelper;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.helper.PageableHelper;
import com.project.schoolmanagment.service.user.UserService;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MethodHelper methodHelper;
    private final DateTimeValidator dateTimeValidator;
    private final UserService userService;
    private final MeetingMapper meetingMapper;
    private final MeetingHelper meetingHelper;
    private final PageableHelper pageableHelper;




    public ResponseMessage<MeetingResponse> saveMeeting(HttpServletRequest servletRequest, MeetingRequest meetingRequest) {

        String username = (String) servletRequest.getAttribute("username");
        User teacher = methodHelper.loadUserByName(username);
        methodHelper.checkIsAdvisor(teacher);

        dateTimeValidator.checkTimeWithException(meetingRequest.getStartTime(),meetingRequest.getStopTime());

        meetingHelper.checkMeetingConflicts(
                meetingRequest.getStudentIds(),
                teacher.getId(),
                meetingRequest.getDate(),
                meetingRequest.getStartTime(),
                meetingRequest.getStopTime()
        );

        List<User> students = methodHelper.getUserList(meetingRequest.getStudentIds());

        Meet meet = meetingMapper.mapMeetRequestToMeet(meetingRequest);
        meet.setStudentList(students);
        meet.setAdvisoryTeacher(teacher);

        Meet savedMeet = meetingRepository.save(meet);

        return  ResponseMessage.<MeetingResponse> builder()
                .message(SuccessMessages.MEET_SAVE)
                .returnBody(meetingMapper.mapMeetToMeetingResponse(savedMeet))
                .httpStatus(HttpStatus.CREATED)
                .build();



    }


    public ResponseMessage<MeetingResponse> updateMeeting(MeetingRequest meetingRequest,
                                                          Long meetingId,
                                                          HttpServletRequest servletRequest){
        Meet meet = meetingHelper.isMeetingExistingById(meetingId);
        meetingHelper.isMeetingMatchWithTeacher(meet,servletRequest);
        dateTimeValidator.checkTimeWithException(meetingRequest.getStartTime(),meetingRequest.getStopTime());

        meetingHelper.checkMeetingConflicts(
                meetingRequest.getStudentIds(),
                meet.getAdvisoryTeacher().getAdvisorTeacherId(),
                meetingRequest.getDate(),
                meetingRequest.getStartTime(),
                meetingRequest.getStopTime()
        );

        List<User> students = methodHelper.getUserList(meetingRequest.getStudentIds());

        Meet meetToUpdate = meetingMapper.mapMeetRequestToMeet(meetingRequest);
        meetToUpdate.setStudentList(students);
        meetToUpdate.setId(meetingId);
        meetToUpdate.setAdvisoryTeacher(meet.getAdvisoryTeacher());

        Meet updatedMeet = meetingRepository.save(meetToUpdate);

        return ResponseMessage.<MeetingResponse>builder()
                .message(SuccessMessages.MEET_UPDATE)
                .returnBody(meetingMapper.mapMeetToMeetingResponse(updatedMeet))
                .httpStatus(HttpStatus.OK)
                .build();
    }


    public List<MeetingResponse> getAll() {
        return meetingRepository
                .findAll()
                .stream()
                .map(meetingMapper::mapMeetToMeetingResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<MeetingResponse> findById(Long id) {
        Meet meet = meetingHelper.isMeetingExistingById(id);

        return ResponseMessage.<MeetingResponse>builder()
                .message(SuccessMessages.MEET_FOUND)
                .returnBody(meetingMapper.mapMeetToMeetingResponse(meet))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage deleteById(Long id) {

        meetingHelper.isMeetingExistingById(id);
        meetingRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.MEET_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public List<MeetingResponse> getAllMeetingByLoggedInTeacher(HttpServletRequest servletRequest) {

        String username = (String) servletRequest.getAttribute("username");
        User teacher = methodHelper.loadUserByName(username);
        methodHelper.checkIsAdvisor(teacher);

        return meetingRepository
                .getByAdvisoryTeacher_IdEquals(teacher.getId())
                .stream()
                .map(meetingMapper::mapMeetToMeetingResponse)
                .collect(Collectors.toList());
    }

    public List<MeetingResponse> getAllMeetingByLoggedInStudent(HttpServletRequest servletRequest) {

        String username = (String) servletRequest.getAttribute("username");
        User student = methodHelper.loadUserByName(username);

        List<Meet> meets =  meetingRepository
                .findByStudentList_IdEquals(student.getId());

        for (Meet meet : meets){
            meet.setStudentList(new ArrayList<>());
        }


        return meets
                .stream()
                .map(meetingMapper::mapMeetToMeetingResponse)
                .collect(Collectors.toList());
    }

    public Page<MeetingResponse> getAllByPage(int page, int size) {

        Pageable pageable =pageableHelper.getPageableWithProperties(page,size);

        return meetingRepository
                        .findAll(pageable)
                        .map(meetingMapper::mapMeetToMeetingResponse);
    }

    public Page<MeetingResponse> getAllByPageByTeacher(int page, int size, HttpServletRequest servletRequest) {

        String username = (String) servletRequest.getAttribute("username");
        User teacher = methodHelper.loadUserByName(username);
        methodHelper.checkIsAdvisor(teacher);

        Pageable pageable =pageableHelper.getPageableWithProperties(page,size);

        return meetingRepository
                .findByAdvisoryTeacher_IdEquals(teacher.getId(),pageable)
                .map(meetingMapper::mapMeetToMeetingResponse);



    }
}
