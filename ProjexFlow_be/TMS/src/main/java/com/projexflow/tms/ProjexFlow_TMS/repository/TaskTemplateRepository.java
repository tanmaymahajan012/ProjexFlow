package com.projexflow.tms.ProjexFlow_TMS.repository;

import com.projexflow.tms.ProjexFlow_TMS.entity.TaskTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, Long> {

    Optional<TaskTemplate> findByIdAndMentorIdAndBatchId(Long id, Long mentorId, Long batchId);

    Optional<TaskTemplate> findByIdAndMentorId(Long id, Long mentorId);

    List<TaskTemplate> findAllByMentorIdAndBatchIdOrderByCreatedAtDesc(Long mentorId, Long batchId);
}

