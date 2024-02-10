package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.contactmessage.dto.ResponseMessage;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.LessonMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.LessonRequest;
import com.project.schoolmanagment.payload.response.businnes.LessonResponse;
import com.project.schoolmanagment.repository.business.LessonRepository;
import com.project.schoolmanagment.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    private final LessonMapper lessonMapper;

    private final PageableHelper pageableHelper;


    public ResponseMessage<LessonResponse> saveLesson(LessonRequest lessonRequest) {
        // lessons must be unique
        isLessonExistsByLessonName(lessonRequest.getLessonName());

        Lesson lessonToSave = lessonMapper.mapLessonRequestToLesson(lessonRequest);

        Lesson savedLesson = lessonRepository.save(lessonToSave);

        return ResponseMessage.<LessonResponse> builder()
                .message(SuccessMessages.LESSON_SAVE)
                .object(lessonMapper.mapLessonToLessonResponse(savedLesson))
                .httpStatus(HttpStatus.CREATED)
                .build();

    }


    private void isLessonExistsByLessonName(String lessonName){

        if (lessonRepository.getByLessonNameEqualsIgnoreCase(lessonName).isPresent()){
            throw new ResourceNotFoundException(String.format(ErrorMessages.ALREADY_CREATED_LESSON_MESSAGE,lessonName));
        }
    }

    public ResponseMessage deleteById(Long id) {
        isLessonExistsById(id);
        lessonRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.LESSON_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();
    }

     Lesson isLessonExistsById(Long id){
        return lessonRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE,id))
        );
    }

    public ResponseMessage<LessonResponse> getLessonByName(String lessonName) {
        if (lessonRepository.getByLessonNameEqualsIgnoreCase(lessonName).isPresent()){
           return ResponseMessage.<LessonResponse> builder()
                   .message(SuccessMessages.LESSON_FOUND)
                   .object(lessonMapper.mapLessonToLessonResponse(lessonRepository.getByLessonNameEqualsIgnoreCase(lessonName).get()))
                   .build();
        }else {
            return ResponseMessage.<LessonResponse> builder()
                    .message(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE,lessonName))
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    public Page<LessonResponse> getLessonByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page,size,sort,type);
        return lessonRepository
                .findAll(pageable)
                .map(lessonMapper::mapLessonToLessonResponse);
    }

    public Set<Lesson> getLessonByIdSet(Set<Long> idSet) {

        return idSet
                .stream()
                .map(this::isLessonExistsById)
                .collect(Collectors.toSet());
    }


    public LessonResponse updateLessonById(Long lessonId, LessonRequest lessonRequest) {

        Lesson lesson = isLessonExistsById(lessonId);

        if (!lesson.getLessonName().equalsIgnoreCase(lessonRequest.getLessonName())){
            isLessonExistsByLessonName(lessonRequest.getLessonName());
        }

        Lesson updatedLesson = lessonMapper.mapLessonRequestToLesson(lessonRequest);
        updatedLesson.setId(lessonId);
        updatedLesson.setLessonPrograms(lesson.getLessonPrograms());


        Lesson savedLesson = lessonRepository.save(updatedLesson);

        return lessonMapper.mapLessonToLessonResponse(savedLesson);
    }
}
