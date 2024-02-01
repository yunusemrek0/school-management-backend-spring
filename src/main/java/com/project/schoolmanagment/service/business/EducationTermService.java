package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.contactmessage.dto.ResponseMessage;
import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.exception.BadRequestException;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.EducationTermMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.EducationTermRequest;
import com.project.schoolmanagment.payload.response.businnes.EducationTermResponse;
import com.project.schoolmanagment.repository.business.EducationTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationTermService {

    private final EducationTermRepository educationTermRepository;

    private final EducationTermMapper educationTermMapper;


    public ResponseMessage<EducationTermResponse> saveEducationTerm(EducationTermRequest educationTermRequest) {

        validateEducationTermDates(educationTermRequest);

        EducationTerm educationTerm =educationTermMapper.mapEducatinTermRequestToEducationTerm(educationTermRequest);

        return ResponseMessage.<EducationTermResponse>builder()
                .message(SuccessMessages.EDUCATION_TERM_SAVE)
                .object(educationTermMapper.mapEducationTermToEducationTermResponse(educationTerm))
                .httpStatus(HttpStatus.CREATED)
                .build();

    }

    private void validateEducationTermDates(EducationTermRequest request){
        validateEducationTermDatesForRequest(request);
        // only one education term can exist in a year
        if (educationTermRepository.existsByTermAndYear(request.getTerm(),request.getStartDate().getYear())){
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_TERM_IS_ALREADY_EXIST_BY_TERM_AND_YEAR_MESSAGE);
        }

        // validate not to have any conflict with other education terms
        if(educationTermRepository.findByYear(request.getStartDate().getYear())
                .stream()
                .anyMatch(educationTerm ->
                        (educationTerm.getStartDate().equals(request.getStartDate()) // et1(10 kasim 2023) / et2(10 kasim 2023)
                                || (educationTerm.getStartDate().isBefore(request.getStartDate())
                                && educationTerm.getEndDate().isAfter(request.getStartDate())) // et1 ( baslama 10 kasim 20203 - bitme 20 kasim 20203)  - et2 ( baslama 15 kasim 2023 bitme 25 kasim 20203)
                                || (educationTerm.getStartDate().isBefore(request.getEndDate())
                                && educationTerm.getEndDate().isAfter(request.getEndDate()))
                                || (educationTerm.getStartDate().isAfter(request.getStartDate())   // arada olma durumu
                                && educationTerm.getEndDate().isBefore(request.getEndDate()))))) {
            throw new BadRequestException(ErrorMessages.EDUCATION_TERM_CONFLICT_MESSAGE);
        }

    }

    private void validateEducationTermDatesForRequest(EducationTermRequest request){

        //registration and start date

        if (request.getLastRegistrationDate().isAfter(request.getStartDate())){
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
        }

        //end date > start date
        if (request.getEndDate().isBefore(request.getStartDate())){
            throw new ResourceNotFoundException(ErrorMessages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
        }

    }
}
