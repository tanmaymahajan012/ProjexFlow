package com.projexflow.tms.ProjexFlow_TMS.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "task_submissions",
        indexes = {
                @Index(name = "idx_submissions_assignment", columnList = "assignment_id"),
                @Index(name = "idx_submissions_group_batch", columnList = "group_id,batch_id"),
                @Index(name = "idx_submissions_student", columnList = "submitted_by_student_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TaskSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private TaskAssignment assignment;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "submitted_by_student_id", nullable = false)
    private Long submittedByStudentId;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "repo_url", length = 500)
    private String repoUrl;

    @Column(name = "pr_url", length = 500)
    private String prUrl;

    @PrePersist
    void prePersist() {
        this.submittedAt = (this.submittedAt == null) ? LocalDateTime.now() : this.submittedAt;
    }
}

