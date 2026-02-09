package com.projexflow.ns.ProjexFlow_NMS.service;

import com.projexflow.ns.ProjexFlow_NMS.dto.NotificationCreateRequest;
import com.projexflow.ns.ProjexFlow_NMS.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    Long create(NotificationCreateRequest req);

    List<NotificationResponse> getForRecipient(Long recipientId, String recipientRole);

    NotificationResponse markRead(Long id, Long recipientId, String recipientRole);

    void deleteNotification(Long id, Long recipientId, String recipientRole);
}
