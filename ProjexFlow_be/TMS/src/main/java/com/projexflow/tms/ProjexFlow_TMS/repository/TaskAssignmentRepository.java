package com.projexflow.tms.ProjexFlow_TMS.repository;
import com.projexflow.tms.ProjexFlow_TMS.entity.AssignmentState;
import com.projexflow.tms.ProjexFlow_TMS.entity.AssignmentStatus;
import com.projexflow.tms.ProjexFlow_TMS.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    Optional<TaskAssignment> findByIdAndMentorId(Long id, Long mentorId);

    Optional<TaskAssignment> findByTask_IdAndGroupId(Long taskId, Long groupId);

    List<TaskAssignment> findAllByMentorIdAndBatchIdAndAssignmentStatusOrderByUpdatedAtDesc(
            Long mentorId, Long batchId, AssignmentStatus status
    );

    List<TaskAssignment> findAllByMentorIdAndBatchIdAndStateAndAssignmentStatusOrderByUpdatedAtDesc(
            Long mentorId, Long batchId, AssignmentState state, AssignmentStatus status
    );

    List<TaskAssignment> findAllByGroupIdAndBatchIdAndAssignmentStatusOrderByUpdatedAtDesc(
            Long groupId, Long batchId, AssignmentStatus status
    );

    /** Dashboard metric: how many tasks are assigned to a group in a batch. */
    long countByGroupIdAndBatchIdAndAssignmentStatus(Long groupId, Long batchId, AssignmentStatus status);

    /** Dashboard metric: how many tasks are in a given state for a group in a batch (status always ASSIGNED). */
    long countByGroupIdAndBatchIdAndAssignmentStatusAndState(Long groupId, Long batchId, AssignmentStatus status, AssignmentState state);

    @Query("""
        select count(a) from TaskAssignment a
        where a.mentorId = :mentorId
          and a.batchId = :batchId
          and a.assignmentStatus = :status
          and a.state = :state
    """)
    long countByMentorAndBatchAndState(Long mentorId, Long batchId, AssignmentStatus status, AssignmentState state);
}

