package com.projexflow.als.ProjexFlow_ALS.repository;

import com.projexflow.als.ProjexFlow_ALS.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Page<ActivityLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, String entityId, Pageable pageable);

    Page<ActivityLog> findBySourceServiceOrderByCreatedAtDesc(String sourceService, Pageable pageable);
}
