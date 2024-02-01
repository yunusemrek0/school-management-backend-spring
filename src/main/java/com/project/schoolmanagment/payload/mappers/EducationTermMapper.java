package com.project.schoolmanagment.payload.mappers;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.payload.request.business.EducationTermRequest;
import com.project.schoolmanagment.payload.response.businnes.EducationTermResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EducationTermMapper {

    public EducationTerm mapEducatinTermRequestToEducationTerm(EducationTermRequest educationTermRequest){
        return EducationTerm.builder()
                .term(educationTermRequest.getTerm())
                .startDate(educationTermRequest.getStartDate())
                .endDate(educationTermRequest.getEndDate())
                .lastRegistrationDate(educationTermRequest.getLastRegistrationDate())
                .build();
    }

    public EducationTermResponse mapEducationTermToEducationTermResponse(EducationTerm educationTerm){

        return EducationTermResponse.builder()
                .id(educationTerm.getId())
                .term(educationTerm.getTerm())
                .startDate(educationTerm.getStartDate())
                .endDate(educationTerm.getEndDate())
                .lastRegistrationDate(educationTerm.getLastRegistrationDate())
                .build();
    }
}
