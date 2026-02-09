//package com.projexflow.UMS.ProjexFlow_UMS.repository;
//
//import com.projexflow.UMS.ProjexFlow_UMS.entity.Admin;
//import com.projexflow.UMS.ProjexFlow_UMS.entity.BaseUser;
//import com.projexflow.UMS.ProjexFlow_UMS.entity.Mentor;
//import com.projexflow.UMS.ProjexFlow_UMS.entity.Student;
//import jakarta.validation.constraints.NotBlank;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//import org.springframework.data.repository.query.Param;
//
//public interface UserRepo extends JpaRepository<BaseUser, UUID> {
//
//    boolean existsByEmailIgnoreCase(String email);
//
//    // subclass field checks using JPQL (works because it targets the subclass directly)
//    @Query("select (count(s) > 0) from Student s where s.prn = :prn")
//    boolean existsStudentByPrn(@Param("prn") String prn);
//
//    @Query("select (count(m) > 0) from Mentor m where m.empId = :empId")
//    boolean existsMentorByEmpId(@Param("empId") String empId);
//
//    // use NATIVE queries for listing users by type (no JPQL entity name issues)
//    @Query(value = "SELECT * FROM users WHERE user_type = 'STUDENT'", nativeQuery = true)
//    List<Student> findAllStudents();
//
//    @Query(value = "SELECT * FROM users WHERE user_type = 'MENTOR'", nativeQuery = true)
//    List<Mentor> findAllMentors();
//
//    @Query(value = "SELECT * FROM users WHERE user_type = 'ADMIN'", nativeQuery = true)
//    List<Admin> findAllAdmins();
//
//}
//
