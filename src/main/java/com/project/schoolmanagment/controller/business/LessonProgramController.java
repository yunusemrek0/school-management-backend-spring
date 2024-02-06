package com.project.schoolmanagment.controller.business;


import com.project.schoolmanagment.payload.request.business.LessonProgramRequest;
import com.project.schoolmanagment.payload.response.businnes.LessonProgramResponse;
import com.project.schoolmanagment.payload.response.businnes.LessonResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.service.business.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/lessonPrograms")
@RequiredArgsConstructor
public class LessonProgramController {

    private final LessonProgramService lessonProgramService;


    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @PostMapping("/save")
    public ResponseMessage<LessonProgramResponse> saveLessonProgram(
            @RequestBody @Valid LessonProgramRequest lessonProgramRequest){

        return lessonProgramService.saveLessonProgram(lessonProgramRequest);

    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Student','Teacher')")
    @GetMapping("/getAll")
    public List<LessonProgramResponse> getAllLessonProgramByList(){
        return lessonProgramService.getAll();
    }


    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Student','Teacher')")
    @GetMapping("/getAllUnassigned")
    public List<LessonProgramResponse> getAllUnassigned(){
        return lessonProgramService.getAllUnassigned();
    }


    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Student','Teacher')")
    @GetMapping("/getAllAssigned")
    public List<LessonProgramResponse> getAllAssigned(){
        return lessonProgramService.getAllAssigned();
    }


    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @GetMapping("/getById/{id}")
    public LessonProgramResponse getById(@PathVariable Long id){
        return lessonProgramService.getById(id);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @DeleteMapping("/deleteById/{id}")
    public ResponseMessage deleteById(@PathVariable Long id){
        return lessonProgramService.deleteById(id);
    }


    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Student','Teacher')")
    @GetMapping("/getByPage")
    public Page<LessonProgramResponse> getLessonProgramByPage(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "day") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type){

        return lessonProgramService.getLessonProgramByPage(page,size,sort,type);
    }


    @PreAuthorize("hasAnyAuthority('Teacher')")
    @GetMapping("/getAllByTeacher")
    public Set<LessonProgramResponse> getAllLessonProgramsByTeacherUsername(HttpServletRequest httpServletRequest){
        return lessonProgramService.getAllLessonProgramsByTeacherUsername(httpServletRequest);
    }



    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @GetMapping("/getAllByTeacher/{id}")
    public Set<LessonProgramResponse> getAllLessonProgramsByTeacherId(@PathVariable Long id){
        return lessonProgramService.getAllLessonProgramsByTeacherId(id);
    }


}
