package com.projexflow.ns.ProjexFlow_NMS.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "notifications",
        indexes = {
                @Index(name="idx_notifications_recipient", columnList = "recipientId,recipientRole"),
                @Index(name="idx_notifications_created", columnList = "createdAt")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recipientId;

    /** STUDENT / MENTOR / ADMIN */
    @Column(nullable = false, length = 20)
    private String recipientRole;

    /** e.g. GROUP_INVITE, TASK_ASSIGNED, SUBMISSION_SENT, SUBMISSION_REVIEWED */
    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(length = 60)
    private String referenceType;

    private Long referenceId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant readAt;
}
