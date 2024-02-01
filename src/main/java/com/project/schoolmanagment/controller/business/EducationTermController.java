package com.project.schoolmanagment.controller.business;

import com.project.schoolmanagment.contactmessage.dto.ResponseMessage;
import com.project.schoolmanagment.payload.request.business.EducationTermRequest;
import com.project.schoolmanagment.payload.response.businnes.EducationTermResponse;
import com.project.schoolmanagment.service.business.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/educationTerm")
@RequiredArgsConstructor
public class EducationTermController {


    private final EducationTermService educationTermService;

    @PreAuthorize("hasAnyAuthority('Admin','Dean')")
    @PostMapping("/save")
    public ResponseMessage<EducationTermResponse> saveEducationTerm(@Valid @RequestBody
                                                                    EducationTermRequest educationTermRequest){
        return educationTermService.saveEducationTerm(educationTermRequest);
    }

}
