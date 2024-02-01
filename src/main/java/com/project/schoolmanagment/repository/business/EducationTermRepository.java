package com.project.schoolmanagment.repository.business;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.enums.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationTermRepository extends JpaRepository<EducationTerm,Long> {

    @Query("SELECT (count (e) > 0) FROM EducationTerm e WHERE e.term=?1 and extract(YEAR FROM e.startDate) =?2")
    boolean existsByTermAndYear(Term term,int year);

    // find all eduTerm in a year
    @Query("SELECT e FROM EducationTerm e WHERE extract(YEAR FROM e.startDate) =?1")
    List<EducationTerm> findByYear(int year);
}
