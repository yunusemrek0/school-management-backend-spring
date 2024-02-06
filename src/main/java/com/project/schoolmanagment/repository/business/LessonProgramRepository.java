package com.project.schoolmanagment.repository.business;

import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LessonProgramRepository extends JpaRepository<LessonProgram,Long> {

    //@Query("SELECT l FROM LessonProgram l JOIN l.users u WHERE u.id = NULL")
    List<LessonProgram> findByUsers_IdNull();

    List<LessonProgram> findByUsers_IdNotNull();


    @Query("SELECT l FROM LessonProgram l inner join l.users users WHERE users.username = ?1")
    Set<LessonProgram> getLessonProgramByUsername(String username);

    @Query("SELECT l FROM LessonProgram l WHERE l.id IN :idSet")
    Set<LessonProgram> getLessonProgramByIdList(Set<Long> idSet);



    Set<LessonProgram> findByUsers_IdEquals(Long userId);



}
