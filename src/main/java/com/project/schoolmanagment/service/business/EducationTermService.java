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
import com.project.schoolmanagment.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationTermService {

    private final EducationTermRepository educationTermRepository;

    private final EducationTermMapper educationTermMapper;

    private final PageableHelper pageableHelper;


    public ResponseMessage<EducationTermResponse> saveEducationTerm(EducationTermRequest educationTermRequest) {

        validateEducationTermDates(educationTermRequest);

        EducationTerm educationTerm =educationTermMapper.mapEducatinTermRequestToEducationTerm(educationTermRequest);

        EducationTerm educationTermToSaved = educationTermRepository.save(educationTerm);

        return ResponseMessage.<EducationTermResponse>builder()
                .message(SuccessMessages.EDUCATION_TERM_SAVE)
                .object(educationTermMapper.mapEducationTermToEducationTermResponse(educationTermToSaved))
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

    public EducationTermResponse findEducationTermById(Long id) {

        EducationTerm educationTerm = isEducationTermExists(id);

        return educationTermMapper.mapEducationTermToEducationTermResponse(educationTerm);
    }

    public EducationTerm isEducationTermExists(Long id){
        return educationTermRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException(String.format(ErrorMessages.EDUCATION_TERM_NOT_FOUND_MESSAGE,id))
        );
    }

    public Page<EducationTermResponse> getAllEducationTermByPage(int page, int size, String sort, String type) {

        Pageable pageable = pageableHelper.getPageableWithProperties(page,size,sort,type);

        return educationTermRepository
                .findAll(pageable)
                .map(educationTermMapper ::mapEducationTermToEducationTermResponse);
    }

    public ResponseMessage deleteById(Long id) {

        isEducationTermExists(id);
        educationTermRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.EDUCATION_TERM_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();


    }

    public ResponseMessage<EducationTermResponse> updateEducationTerm(Long id, EducationTermRequest educationTermRequest) {

        isEducationTermExists(id);

        validateEducationTermDatesForRequest(educationTermRequest);

        EducationTerm educationTermToSave = educationTermMapper.mapEducatinTermRequestToEducationTerm(educationTermRequest);
        educationTermToSave.setId(id);

        EducationTerm savedEducationTerm = educationTermRepository.save(educationTermToSave);

        return ResponseMessage.<EducationTermResponse> builder()
                .message(SuccessMessages.EDUCATION_TERM_UPDATE)
                .httpStatus(HttpStatus.OK)
                .object(educationTermMapper.mapEducationTermToEducationTermResponse(savedEducationTerm))
                .build();
    }

    public List<EducationTermResponse> getAllEducationTerms() {

        return educationTermRepository
                .findAll()
                .stream()
                .map(educationTermMapper :: mapEducationTermToEducationTermResponse)
                .collect(Collectors.toList());
    }
}
