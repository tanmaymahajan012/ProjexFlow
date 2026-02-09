package com.projexflow.tms.ProjexFlow_TMS.repository;


import com.projexflow.tms.ProjexFlow_TMS.entity.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {

    List<TaskSubmission> findAllByAssignment_IdOrderBySubmittedAtDesc(Long assignmentId);

    Optional<TaskSubmission> findTopByAssignment_IdOrderBySubmittedAtDesc(Long assignmentId);

    long countByAssignment_Id(Long assignmentId);
}

