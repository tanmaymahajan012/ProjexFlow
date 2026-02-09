package com.projexflow.als.ProjexFlow_ALS.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "activity_logs",
        indexes = {
                @Index(name = "idx_activity_logs_user", columnList = "userId"),
                @Index(name = "idx_activity_logs_entity", columnList = "entityType,entityId"),
                @Index(name = "idx_activity_logs_created", columnList = "createdAt")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Auth subject info (forwarded by gateway/JWT)
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String userRole;

    private String sourceService;

    @Column(nullable = false)
    private String action;

    private String entityType;
    private String entityId;

    @Column(length = 1000)
    private String description;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
