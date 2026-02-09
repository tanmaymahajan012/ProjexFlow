package com.projexflow.gms.ProjexFlow_GMS.repository;

import com.projexflow.gms.ProjexFlow_GMS.entity.GroupJoinRequest;
import com.projexflow.gms.ProjexFlow_GMS.entity.RequestStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface GroupJoinRequestRepo extends JpaRepository<GroupJoinRequest, Long> {

        List<GroupJoinRequest> findByBatchIdAndCourseIgnoreCaseAndToStudentIdOrderByCreatedAtDesc(
                        Long batchId, String course, Long toStudentId);

        List<GroupJoinRequest> findByBatchIdAndCourseIgnoreCaseAndFromStudentIdOrderByCreatedAtDesc(
                        Long batchId, String course, Long fromStudentId);

        boolean existsByBatchIdAndCourseIgnoreCaseAndFromStudentIdAndToStudentIdAndStatus(
                        Long batchId, String course, Long fromStudentId, Long toStudentId, RequestStatus status);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select r from GroupJoinRequest r where r.id = :id")
        Optional<GroupJoinRequest> findByIdForUpdate(@Param("id") Long id);
}
