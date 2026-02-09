package com.projexflow.als.ProjexFlow_ALS.service;

import com.projexflow.als.ProjexFlow_ALS.dto.ActivityLogResponse;
import com.projexflow.als.ProjexFlow_ALS.dto.CreateActivityLogRequest;
import org.springframework.data.domain.Page;

public interface ActivityLogService {

    ActivityLogResponse create(String userId, String email, String role, CreateActivityLogRequest request);

    Page<ActivityLogResponse> myLogs(String userId, int page, int size);

    Page<ActivityLogResponse> search(String userId, String sourceService, String entityType, String entityId, int page, int size);
}
