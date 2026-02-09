package com.projexflow.pms.ProjexFlow_PMS.repository;

import com.projexflow.pms.ProjexFlow_PMS.entity.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    Optional<Project> findByBatchIdAndGroupId(Long batchId, Long groupId);

    List<Project> findAllByBatchIdAndGroupIdIn(Long batchId, List<Long> groupIds);

    List<Project> findAllByBatchId(Long batchId);
}

