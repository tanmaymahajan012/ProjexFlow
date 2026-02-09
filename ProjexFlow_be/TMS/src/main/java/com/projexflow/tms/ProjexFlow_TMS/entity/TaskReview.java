package com.projexflow.tms.ProjexFlow_TMS.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "task_reviews",
        indexes = {
                @Index(name = "idx_reviews_assignment_time", columnList = "assignment_id,reviewed_at"),
                @Index(name = "idx_reviews_mentor", columnList = "mentor_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TaskReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReviewDecision decision;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_code", length = 40)
    private ReviewReasonCode reasonCode;

    @Lob
    private String comments;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;

    @PrePersist
    void prePersist() {
        this.reviewedAt = (this.reviewedAt == null) ? LocalDateTime.now() : this.reviewedAt;
    }
}

