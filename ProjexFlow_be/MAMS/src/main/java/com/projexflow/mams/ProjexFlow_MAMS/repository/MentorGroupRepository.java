package com.projexflow.mams.ProjexFlow_MAMS.repository;

import com.projexflow.mams.ProjexFlow_MAMS.entity.MentorGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MentorGroupRepository extends JpaRepository<MentorGroup, Long> {

    @Query("select mg.groupId from MentorGroup mg where mg.mentorId = :mentorId and mg.batchId = :batchId")
    List<Long> findGroupIdsByMentorIdAndBatchId(Long mentorId, Long batchId);

    /**
     * Used when building UI-friendly responses that include assignedAt.
     */
    List<MentorGroup> findByMentorIdAndBatchId(Long mentorId, Long batchId);

    java.util.Optional<MentorGroup> findByBatchIdAndGroupId(Long batchId, Long groupId);

    @Modifying
    @Transactional
    @Query("delete from MentorGroup mg where mg.batchId = :batchId")
    void deleteAllByBatchId(Long batchId);

    /** Dashboard metric: how many groups are assigned to a mentor in a batch. */
    long countByMentorIdAndBatchId(Long mentorId, Long batchId);
}
