package com.project.schoolmanagment.controller.business;

import com.project.schoolmanagment.contactmessage.dto.ResponseMessage;
import com.project.schoolmanagment.payload.request.business.EducationTermRequest;
import com.project.schoolmanagment.payload.response.businnes.EducationTermResponse;
import com.project.schoolmanagment.service.business.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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


    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Teacher')")
    @GetMapping("/getAll")
    public List<EducationTermResponse> getAllEducationTerms(){
        return educationTermService.getAllEducationTerms();
    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Teacher')")
    @GetMapping("/getEducationTermById/{id}")
    public EducationTermResponse getEducationTermById(@PathVariable Long id){

        return educationTermService.findEducationTermById(id);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Teacher')")
    @GetMapping("/getEducationTermsByPage")
    public Page<EducationTermResponse> getAllEducationTermsByPage(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "startDate") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type) {

        return educationTermService.getAllEducationTermByPage(page,size,sort,type);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Teacher')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteEducationTermById(@PathVariable Long id){

        return educationTermService.deleteById(id);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Teacher')")
    @PutMapping("/update/{id}")
    public ResponseMessage<EducationTermResponse> updateEducationTerm(@PathVariable Long id,
                                                                      @RequestBody @Valid EducationTermRequest educationTermRequest){
        return educationTermService.updateEducationTerm(id,educationTermRequest);
    }
}
