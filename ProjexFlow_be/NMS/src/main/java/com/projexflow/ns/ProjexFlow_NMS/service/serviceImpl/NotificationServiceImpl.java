package com.projexflow.ns.ProjexFlow_NMS.service.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projexflow.ns.ProjexFlow_NMS.dto.NotificationCreateRequest;
import com.projexflow.ns.ProjexFlow_NMS.dto.NotificationResponse;
import com.projexflow.ns.ProjexFlow_NMS.entity.Notification;
import com.projexflow.ns.ProjexFlow_NMS.repository.NotificationRepository;
import com.projexflow.ns.ProjexFlow_NMS.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Long create(NotificationCreateRequest req) {
        String metaJson = null;
        try {
            if (req.metadata() != null) {
                metaJson = objectMapper.writeValueAsString(req.metadata());
            }
        } catch (Exception ignored) {
        }

        Notification n = Notification.builder()
                .recipientId(req.recipientId())
                .recipientRole(req.recipientRole())
                .type(req.type())
                .title(req.title())
                .message(req.message())
                .referenceType(req.referenceType())
                .referenceId(req.referenceId())
                .metadataJson(metaJson)
                .isRead(false)
                .createdAt(Instant.now())
                .build();

        Notification saved = repo.save(n);

        // Push in real-time to the recipient over WebSocket.
        // The connected websocket Principal name is set in StompAuthChannelInterceptor
        // as ROLE:ID (e.g. STUDENT:12).
        String userKey = (req.recipientRole() == null ? "" : req.recipientRole().trim().toUpperCase())
                + ":" + req.recipientId();
        try {
            messagingTemplate.convertAndSendToUser(userKey, "/queue/notifications", toResponse(saved));
        } catch (Exception ignored) {
            // If websocket delivery fails (user offline, etc.), we still keep the DB
            // record.
        }

        return saved.getId();
    }

    @Override
    public List<NotificationResponse> getForRecipient(Long recipientId, String recipientRole) {
        return repo.findAllByRecipientIdAndRecipientRoleOrderByCreatedAtDesc(recipientId, recipientRole)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public NotificationResponse markRead(Long id, Long recipientId, String recipientRole) {
        Notification n = repo.findById(id).orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!n.getRecipientId().equals(recipientId) || !n.getRecipientRole().equalsIgnoreCase(recipientRole)) {
            throw new RuntimeException("Not allowed");
        }
        if (!n.isRead()) {
            n.setRead(true);
            n.setReadAt(Instant.now());
            repo.save(n);
        }
        return toResponse(n);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id, Long recipientId, String recipientRole) {
        Notification n = repo.findById(id).orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!n.getRecipientId().equals(recipientId) || !n.getRecipientRole().equalsIgnoreCase(recipientRole)) {
            throw new RuntimeException("Not allowed");
        }
        repo.delete(n);
    }

    private NotificationResponse toResponse(Notification n) {
        Map<String, Object> meta = null;
        try {
            if (n.getMetadataJson() != null && !n.getMetadataJson().isBlank()) {
                meta = objectMapper.readValue(n.getMetadataJson(), new TypeReference<>() {
                });
            }
        } catch (Exception ignored) {
        }
        return new NotificationResponse(
                n.getId(),
                n.getRecipientId(),
                n.getRecipientRole(),
                n.getType(),
                n.getTitle(),
                n.getMessage(),
                n.getReferenceType(),
                n.getReferenceId(),
                meta,
                n.isRead(),
                n.getCreatedAt(),
                n.getReadAt());
    }
}
