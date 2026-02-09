package com.projexflow.als.ProjexFlow_ALS.service.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projexflow.als.ProjexFlow_ALS.dto.ActivityLogResponse;
import com.projexflow.als.ProjexFlow_ALS.dto.CreateActivityLogRequest;
import com.projexflow.als.ProjexFlow_ALS.entity.ActivityLog;
import com.projexflow.als.ProjexFlow_ALS.exception.BadRequestException;
import com.projexflow.als.ProjexFlow_ALS.repository.ActivityLogRepository;
import com.projexflow.als.ProjexFlow_ALS.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public ActivityLogResponse create(String userId, String email, String role, CreateActivityLogRequest request) {
        if (userId == null || userId.isBlank()) throw new BadRequestException("Missing actor id");
        if (email == null || email.isBlank()) throw new BadRequestException("Missing X-EMAIL");
        if (role == null || role.isBlank()) throw new BadRequestException("Missing X-ROLE");

        String metadataJson = null;
        try {
            if (request.getMetadata() != null) {
                metadataJson = objectMapper.writeValueAsString(request.getMetadata());
            }
        } catch (Exception e) {
            throw new BadRequestException("metadata must be valid JSON");
        }

        ActivityLog log = ActivityLog.builder()
                .userId(userId)
                .userEmail(email)
                .userRole(role)
                .sourceService(request.getSourceService())
                .action(request.getAction())
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .description(request.getDescription())
                .metadataJson(metadataJson)
                .build();

        ActivityLog saved = repository.save(log);
        return toResponse(saved);
    }

    @Override
    public Page<ActivityLogResponse> myLogs(String userId, int page, int size) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(this::toResponse);
    }

    @Override
    public Page<ActivityLogResponse> search(String userId, String sourceService, String entityType, String entityId, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (entityType != null && !entityType.isBlank() && entityId != null && !entityId.isBlank()) {
            return repository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId, pr).map(this::toResponse);
        }
        if (sourceService != null && !sourceService.isBlank()) {
            return repository.findBySourceServiceOrderByCreatedAtDesc(sourceService, pr).map(this::toResponse);
        }
        // Default: if userId is provided, return that user's logs; otherwise return all logs (admin/mentor only).
        if (userId != null && !userId.isBlank()) {
            return repository.findByUserIdOrderByCreatedAtDesc(userId, pr).map(this::toResponse);
        }
        return repository.findAll(pr).map(this::toResponse);
    }

    private ActivityLogResponse toResponse(ActivityLog log) {
        return ActivityLogResponse.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .userEmail(log.getUserEmail())
                .userRole(log.getUserRole())
                .sourceService(log.getSourceService())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .description(log.getDescription())
                .metadata(readMetadata(log.getMetadataJson()))
                .createdAt(log.getCreatedAt())
                .build();
    }

    private Map<String, Object> readMetadata(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
