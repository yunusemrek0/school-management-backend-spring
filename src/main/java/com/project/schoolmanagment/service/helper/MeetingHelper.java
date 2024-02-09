package com.project.schoolmanagment.service.helper;

import com.project.schoolmanagment.entity.concretes.businnes.Meet;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.BadRequestException;
import com.project.schoolmanagment.exception.ConflictException;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.repository.business.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MeetingHelper {

    private final MethodHelper methodHelper;
    private final MeetingRepository meetingRepository;

    public void checkMeetingConflicts(List<Long> studentIdList, Long teacherId, LocalDate meetingDate, LocalTime startTime, LocalTime stopTime){

        List<Meet> studentExistsMeetings = new ArrayList<>();

        for (Long id:studentIdList){
            methodHelper.checkRole(methodHelper.isUserExist(id), RoleType.STUDENT);
            studentExistsMeetings.addAll(meetingRepository.findByStudentList_IdEquals(id));
        }
        studentExistsMeetings.addAll(meetingRepository.getByAdvisoryTeacher_IdEquals(teacherId));

        for (Meet meet:studentExistsMeetings){
            LocalTime existingStartTime = meet.getStartTime();
            LocalTime existingStopTime = meet.getStopTime();

            if (meet.getDate().equals(meetingDate) && (
                    (startTime.isAfter(existingStartTime) && startTime.isBefore(existingStopTime)) ||
                            (stopTime.isAfter(existingStartTime) && stopTime.isBefore(existingStopTime)) ||
                            (startTime.isBefore(existingStartTime) && stopTime.isAfter(existingStopTime)) ||
                            (startTime.equals(existingStartTime) || stopTime.equals(existingStopTime))

            ) ){
                throw new ConflictException(ErrorMessages.MEET_HOURS_CONFLICT);
            }
        }

    }

    public void isMeetingMatchWithTeacher(Meet meet, HttpServletRequest servletRequest){

        String username = (String) servletRequest.getAttribute("username");
        User teacher = methodHelper.loadUserByName(username);
        methodHelper.checkIsAdvisor(teacher);

        if (! meet.getAdvisoryTeacher().getId().equals(teacher.getId())){
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }

    }

    public Meet isMeetingExistingById(Long id){
        return meetingRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException(String.format(ErrorMessages.MEET_NOT_FOUND_MESSAGE,id))
        );
    }

}
