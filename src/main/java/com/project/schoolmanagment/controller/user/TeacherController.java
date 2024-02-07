package com.project.schoolmanagment.controller.user;

import com.project.schoolmanagment.payload.request.user.TeacherRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.TeacherResponse;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.service.user.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;


    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<ResponseMessage<UserResponse>> saveTeacher(@RequestBody @Valid TeacherRequest teacherRequest){
        return ResponseEntity.ok(teacherService.saveTeacher(teacherRequest));
    }


    @GetMapping("/changeAdvisorTeacherStatus/{id}")
    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    public ResponseMessage<UserResponse> changeAdvisorTeacherStatus(@PathVariable Long id){
        return teacherService.changeAdvisorTeacherStatus(id);
    }


    @GetMapping("/getAllAdvisorTeacher")
    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    public List<UserResponse> getAllAdvisorTeacher(){
        return teacherService.getAllAdvisorTeacher();
    }



    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    public ResponseMessage<UserResponse> updateTeacherByManagers(@RequestBody @Valid TeacherRequest teacherRequest,
                                                                 @PathVariable Long id){
        return teacherService.updateTeacherByManagers(teacherRequest,id);
    }

}
