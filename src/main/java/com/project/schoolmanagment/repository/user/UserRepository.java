package com.project.schoolmanagment.repository.user;

import com.project.schoolmanagment.entity.concretes.user.User;
import java.util.List;

import com.project.schoolmanagment.entity.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {  
  
  boolean existsByUsername(String username);
  
  boolean existsBySsn(String ssn);
  
  boolean existsByPhoneNumber(String phoneNumber);
  
  boolean existsByEmail(String email);
  
  List<User>getUserByNameContaining(String userName);
  
  User findByUsername(String userName);

  @Query("SELECT u FROM User u WHERE u.userRole.roleName = :roleName") 
  Page<User> findByUserByRole(@Param("roleName")String roleName, Pageable pageable);


  List<User> findByAdvisorTeacherId(Long id);

  @Query("SELECT (COUNT (u) > 0) FROM User u WHERE u.userRole.roleType = ?1")
  boolean findStudent(RoleType roleType);

  @Query("SELECT MAX(u.studentNumber) FROM User u")
  int getMaxStudentNumber();

  @Query("SELECT u FROM User u WHERE u.isAdvisor IS true ")
  List<User> findAllByAdvisorTeacher();

}
