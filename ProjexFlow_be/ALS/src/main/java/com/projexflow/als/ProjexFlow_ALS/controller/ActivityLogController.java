package com.projexflow.als.ProjexFlow_ALS.controller;

import com.projexflow.als.ProjexFlow_ALS.dto.ActivityLogResponse;
import com.projexflow.als.ProjexFlow_ALS.dto.CreateActivityLogRequest;
import com.projexflow.als.ProjexFlow_ALS.identity.CurrentUserResolver;
import com.projexflow.als.ProjexFlow_ALS.service.ActivityLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/als/logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService service;
    private final CurrentUserResolver currentUser;

    @PostMapping
    public ActivityLogResponse create(
            @RequestBody @Valid CreateActivityLogRequest request
    ) {
        String role = currentUser.role();
        String email = currentUser.email();
        String actorId = String.valueOf(currentUser.roleSpecificId());
        return service.create(actorId, email, role, request);
    }

    @GetMapping("/my")
    public Page<ActivityLogResponse> myLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String actorId = String.valueOf(currentUser.roleSpecificId());
        return service.myLogs(actorId, page, size);
    }

    /**
     * Admin/Mentor search endpoint:
     * - filter by userId (optional)
     * - or by entityType+entityId (optional)
     * - or by sourceService (optional)
     */
    @GetMapping
    public Page<ActivityLogResponse> search(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sourceService,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.search(userId, sourceService, entityType, entityId, page, size);
    }
}
