package com.projexflow.ns.ProjexFlow_NMS.controller;

import com.projexflow.ns.ProjexFlow_NMS.dto.NotificationCreateRequest;
import com.projexflow.ns.ProjexFlow_NMS.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Internal endpoint used by other services to create notifications.
 */
@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
public class InternalNotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@RequestBody NotificationCreateRequest request) {
        Long id = notificationService.create(request);
        return Map.of("id", id);
    }
}
