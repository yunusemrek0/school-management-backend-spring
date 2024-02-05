package com.project.schoolmanagment.controller.business;

import com.project.schoolmanagment.contactmessage.dto.ResponseMessage;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.payload.request.business.LessonRequest;
import com.project.schoolmanagment.payload.response.businnes.LessonResponse;
import com.project.schoolmanagment.service.business.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @PostMapping("/save")
    public ResponseMessage<LessonResponse> saveLesson(@RequestBody @Valid LessonRequest lessonRequest){
        return lessonService.saveLesson(lessonRequest);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteLessonById(@PathVariable Long id){
        return lessonService.deleteById(id);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @GetMapping("/getLessonByName}")
    public ResponseMessage<LessonResponse> getLessonByName(@RequestParam String lessonName){
        return lessonService.getLessonByName(lessonName);
    }


    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @GetMapping("/getLessonByPage}")
    public Page<LessonResponse> getLessonByPage(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "lessonName") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type){

        return lessonService.getLessonByPage(page,size,sort,type);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @GetMapping("/getLessonAllByIdSet}")
    public Set<Lesson> getAllLessonByIdSet(@RequestParam(name = "lessonId") Set<Long> idSet){

        return lessonService.getLessonByIdSet(idSet);
    }


    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
    @PutMapping("/update/{lessonId}")
    public ResponseEntity<LessonResponse> updateLessonById(@PathVariable Long lessonId,
                                                           @RequestBody @Valid LessonRequest lessonRequest){

        return ResponseEntity.ok(lessonService.updateLessonById(lessonId,lessonRequest));
    }



}
