package com.projexflow.tms.ProjexFlow_TMS.repository;

import com.projexflow.tms.ProjexFlow_TMS.entity.TaskReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskReviewRepository extends JpaRepository<TaskReview, Long> {

    List<TaskReview> findAllByAssignmentIdOrderByReviewedAtDesc(Long assignmentId);

    Optional<TaskReview> findTopByAssignmentIdOrderByReviewedAtDesc(Long assignmentId);
}

