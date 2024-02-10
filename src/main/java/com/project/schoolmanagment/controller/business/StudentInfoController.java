package com.project.schoolmanagment.controller.business;

import com.project.schoolmanagment.payload.request.business.StudentInfoRequest;
import com.project.schoolmanagment.payload.request.business.StudentInfoUpdateRequest;
import com.project.schoolmanagment.payload.response.businnes.LessonResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.businnes.StudentInfoResponse;
import com.project.schoolmanagment.service.business.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/studentInfo")
@RequiredArgsConstructor
public class StudentInfoController {

    private final StudentInfoService studentInfoService;


    @PreAuthorize("hasAnyAuthority('Teacher')")
    @PostMapping("/save")
    public ResponseMessage<StudentInfoResponse> saveStudentInfo(
            HttpServletRequest servletRequest,
            @RequestBody @Valid StudentInfoRequest studentInfoRequest
            ){
        return studentInfoService.saveStudentInfo(servletRequest,studentInfoRequest);
    }


    @PreAuthorize("hasAnyAuthority('Admin','Teacher')")
    @PutMapping("/update/{id}")
    public ResponseMessage<StudentInfoResponse> updateStudentInfo(
            @PathVariable Long id,
            @RequestBody @Valid StudentInfoUpdateRequest studentInfoRequest
    ){
        return studentInfoService.updateStudentInfo(id,studentInfoRequest);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Teacher')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteById(@PathVariable Long id){
        return studentInfoService.deleteById(id);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @GetMapping("/getStudentInfoByPage")
    public Page<StudentInfoResponse> getStudentInfoByPage(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "absentee") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type){

        return studentInfoService.getLessonByPage(page,size,sort,type);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @GetMapping("/getById/{id}")
    public ResponseMessage<StudentInfoResponse> getById(@PathVariable Long id){
        return studentInfoService.getById(id);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @GetMapping("/getByStudentId/{id}")
    public ResponseEntity<List<StudentInfoResponse>> getByStudentId(@PathVariable Long id){
        return ResponseEntity.ok(studentInfoService.getByStudentId(id));
    }
}
