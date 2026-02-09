package com.projexflow.UMS.ProjexFlow_UMS.repository;


import com.projexflow.UMS.ProjexFlow_UMS.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPrn(String prn);
    Optional<Student> findByPrn(String prn);
    List<Student> findByBatchIdAndCourse(Long batchId, String course);
    @Query("select distinct s.batchId from Student s where s.active = true order by s.batchId")
    List<Long> findDistinctActiveBatchIds();
    @Query("""
       select distinct s.course
       from Student s
       where s.active = true
         and s.course is not null
       order by s.course
       """)
    List<String> findDistinctActiveCourses();
    /**
     * Used by internal services (e.g., GMS) to list all students in a batch.
     */
    List<Student> findByBatchId(Long batchId);

    /** Dashboard metric: active students. */
    long countByActiveTrue();
}
