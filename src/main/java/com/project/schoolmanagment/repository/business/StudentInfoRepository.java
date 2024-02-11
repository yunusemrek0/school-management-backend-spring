package com.project.schoolmanagment.repository.business;

import com.project.schoolmanagment.entity.concretes.businnes.StudentInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentInfoRepository extends JpaRepository<StudentInfo,Long> {


    @Query("SELECT (count (s) > 0) FROM StudentInfo s WHERE s.lesson.lessonName=?1 and s.student.id=?2")
    boolean giveMeDuplications(String lessonName,Long id);


    @Query("SELECT s FROM StudentInfo s WHERE s.student.id=?1")
    List<StudentInfo> findByStudentInfo(Long id);



}
