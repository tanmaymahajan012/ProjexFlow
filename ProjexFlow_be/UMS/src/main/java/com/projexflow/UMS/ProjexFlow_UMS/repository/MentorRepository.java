package com.projexflow.UMS.ProjexFlow_UMS.repository;

import com.projexflow.UMS.ProjexFlow_UMS.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    Optional<Mentor> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmpId(String empId);

    java.util.List<Mentor> findAllByCourseIgnoreCaseAndActiveTrue(String course);

    /** Dashboard metric: active mentors. */
    long countByActiveTrue();
}
