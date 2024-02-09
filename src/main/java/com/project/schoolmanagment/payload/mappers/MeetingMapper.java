package com.project.schoolmanagment.payload.mappers;

import com.project.schoolmanagment.entity.concretes.businnes.Meet;
import com.project.schoolmanagment.payload.request.business.MeetingRequest;
import com.project.schoolmanagment.payload.response.businnes.MeetingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetingMapper {

    public Meet mapMeetRequestToMeet(MeetingRequest meetingRequest){
        return Meet.builder()
                .date(meetingRequest.getDate())
                .description(meetingRequest.getDescription())
                .startTime(meetingRequest.getStartTime())
                .stopTime(meetingRequest.getStopTime())
                .build();
    }

    public MeetingResponse mapMeetToMeetingResponse(Meet meet){
        return MeetingResponse.builder()
                .id(meet.getId())
                .startTime(meet.getStartTime())
                .stopTime(meet.getStopTime())
                .description(meet.getDescription())
                .advisorTeacherId(meet.getAdvisoryTeacher().getId())
                .teacherSsn(meet.getAdvisoryTeacher().getSsn())
                .teacherName(meet.getAdvisoryTeacher().getName())
                .students(meet.getStudentList())
                .build();
    }
}
