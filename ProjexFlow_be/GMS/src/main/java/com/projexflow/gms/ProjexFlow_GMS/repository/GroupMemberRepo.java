package com.projexflow.gms.ProjexFlow_GMS.repository;

import com.projexflow.gms.ProjexFlow_GMS.entity.GroupMember;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface GroupMemberRepo extends JpaRepository<GroupMember, Long> {

    @Query("""
        select m.groupId from GroupMember m
        where m.batchId = :batchId and m.studentId = :studentId and m.active = true
    """)
    Optional<Long> findActiveGroupId(@Param("batchId") Long batchId, @Param("studentId") Long studentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from GroupMember m where m.groupId = :groupId and m.active = true")
    List<GroupMember> findActiveMembersForUpdate(@Param("groupId") Long groupId);

    @Query("select count(m) from GroupMember m where m.groupId = :groupId and m.active = true")
    long countActiveMembers(@Param("groupId") Long groupId);

    List<GroupMember> findAllByGroupIdAndActiveTrue(Long groupId);

    boolean existsByGroupIdAndStudentIdAndActiveTrue(Long groupId, Long studentId);

    List<GroupMember> findAllByBatchIdAndActiveTrue(Long batchId);

    List<GroupMember> findAllByBatchIdAndCourseIgnoreCaseAndActiveTrue(Long batchId, String course);
}
