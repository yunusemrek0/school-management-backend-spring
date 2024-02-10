package com.project.schoolmanagment.controller.business;

import com.project.schoolmanagment.payload.request.business.MeetingRequest;
import com.project.schoolmanagment.payload.response.businnes.MeetingResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.service.business.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;


    @PreAuthorize("hasAnyAuthority('Teacher')")
    @PostMapping("/save")
    public ResponseMessage<MeetingResponse> saveMeeting(HttpServletRequest servletRequest,
                                                        @RequestBody @Valid MeetingRequest meetingRequest){
        return meetingService.saveMeeting(servletRequest,meetingRequest);
    }

    @PreAuthorize("hasAnyAuthority('Teacher')")
    @PutMapping("/update/{meetingId}")
    public ResponseMessage<MeetingResponse> updateMeeting(@RequestBody @Valid MeetingRequest meetingRequest,
                                                          @PathVariable Long meetingId,
                                                          HttpServletRequest servletRequest){
        return meetingService.updateMeeting(meetingRequest,meetingId,servletRequest);
    }

    @PreAuthorize("hasAnyAuthority('Admin')")
    @GetMapping("/getAll")
    public List<MeetingResponse> getAll(){
        return meetingService.getAll();
    }

    @PreAuthorize("hasAnyAuthority('Admin')")
    @GetMapping("/findById/{id}")
    public ResponseMessage<MeetingResponse> findById(@PathVariable Long id){
        return meetingService.findById(id);
    }



    @PreAuthorize("hasAnyAuthority('Admin')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteById(@PathVariable Long id){
        return meetingService.deleteById(id);
    }


    @PreAuthorize("hasAnyAuthority('Teacher')")
    @GetMapping("/getAllByLoggedIn")
    public ResponseEntity<List<MeetingResponse>> getAllMeetingByLoggedInTeacher(HttpServletRequest servletRequest){
        return ResponseEntity.ok(meetingService.getAllMeetingByLoggedInTeacher(servletRequest));
    }



    @PreAuthorize("hasAnyAuthority('Student')")
    @GetMapping("/getAllByLoggedInStudent")
    public ResponseEntity<List<MeetingResponse>> getAllMeetingByLoggedInStudent(HttpServletRequest servletRequest){
        return ResponseEntity.ok(meetingService.getAllMeetingByLoggedInStudent(servletRequest));
    }



    @PreAuthorize("hasAnyAuthority('Admin')")
    @GetMapping("/getAllByPage")
    public Page<MeetingResponse> getAllByPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size

    ){
        return meetingService.getAllByPage(page,size);
    }

    @PreAuthorize("hasAnyAuthority('Teacher')")
    @GetMapping("/getAllByPageByTeacher")
    public Page<MeetingResponse> getAllByPageByTeacher(
            HttpServletRequest servletRequest,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size

    ){
        return meetingService.getAllByPageByTeacher(page,size,servletRequest);
    }

}
