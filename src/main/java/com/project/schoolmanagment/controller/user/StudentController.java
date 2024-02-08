package com.project.schoolmanagment.controller.user;


import com.project.schoolmanagment.payload.request.business.ChooseLessonProgramRequest;
import com.project.schoolmanagment.payload.request.user.StudentRequest;
import com.project.schoolmanagment.payload.request.user.StudentUpdateRequestWithoutPassword;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.StudentResponse;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.service.user.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<ResponseMessage<StudentResponse>> saveStudent(@RequestBody @Valid StudentRequest studentRequest){
        return ResponseEntity.ok(studentService.saveStudent(studentRequest));
    }


    @PostMapping("/addLessonProgramToStudent")
    @PreAuthorize("hasAnyAuthority('Student')")
    public ResponseMessage<StudentResponse> addLessonProgram(HttpServletRequest httpServletRequest,
                                                             @RequestBody @Valid ChooseLessonProgramRequest lessonProgramRequest){
        return studentService.addLessonProgram(httpServletRequest,lessonProgramRequest);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyAuthority('Student')")
    public ResponseEntity<String> updateStudent(@RequestBody @Valid StudentUpdateRequestWithoutPassword studentRequest,
                                                HttpServletRequest servletRequest){
        return studentService.updateStudent(studentRequest,servletRequest);
    }



    @PatchMapping("/updateForManagers/{id}")
    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    public ResponseMessage<StudentResponse> updateStudentForManagers(@RequestBody @Valid StudentRequest studentRequest,
                                                @PathVariable Long id){
        return studentService.updateStudentForManagers(studentRequest,id);
    }


    @GetMapping("/changeStatus")
    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    public ResponseMessage changeStatus(@RequestParam Long id,
                                          @RequestParam boolean status){

        return studentService.changeStatus(id,status);
    }
}
