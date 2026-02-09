package com.projexflow.gms.ProjexFlow_GMS.repository;

import com.projexflow.gms.ProjexFlow_GMS.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepo extends JpaRepository<Group, Long> {
    List<Group> findAllByBatchId(Long batchId);

    List<Group> findAllByBatchIdAndCourseIgnoreCase(Long batchId, String course);
}
