package com.projexflow.gms.ProjexFlow_GMS.repository;

import com.projexflow.gms.ProjexFlow_GMS.entity.BatchGroupingState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BatchGroupingStateRepo extends JpaRepository<BatchGroupingState, Long> {

    Optional<BatchGroupingState> findByBatchIdAndCourseIgnoreCase(Long batchId, String course);

    List<BatchGroupingState> findAllByBatchId(Long batchId);
}
