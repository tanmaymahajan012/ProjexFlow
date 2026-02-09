package com.projexflow.ns.ProjexFlow_NMS.controller;

import com.projexflow.ns.ProjexFlow_NMS.dto.NotificationResponse;
import com.projexflow.ns.ProjexFlow_NMS.identity.CurrentUserResolver;
import com.projexflow.ns.ProjexFlow_NMS.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UI endpoints.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserResolver currentUser;

    @GetMapping
    public List<NotificationResponse> myNotifications() {
        String role = currentUser.role();
        Long roleId = currentUser.roleSpecificId();
        return notificationService.getForRecipient(roleId, role);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(
            @PathVariable Long id) {
        String role = currentUser.role();
        Long roleId = currentUser.roleSpecificId();
        return notificationService.markRead(id, roleId, role);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(
            @PathVariable Long id) {
        String role = currentUser.role();
        Long roleId = currentUser.roleSpecificId();
        notificationService.deleteNotification(id, roleId, role);
    }
}
