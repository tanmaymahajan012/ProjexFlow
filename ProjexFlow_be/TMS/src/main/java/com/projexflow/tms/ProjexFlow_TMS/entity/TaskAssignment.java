package com.projexflow.tms.ProjexFlow_TMS.entity;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "task_assignments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_task_group", columnNames = {"task_id", "group_id"})
        },
        indexes = {
                @Index(name = "idx_assignments_task", columnList = "task_id"),
                @Index(name = "idx_assignments_mentor_batch_state", columnList = "mentor_id,batch_id,state"),
                @Index(name = "idx_assignments_group_batch_state", columnList = "group_id,batch_id,state")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TaskAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskTemplate task;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status", nullable = false, length = 20)
    private AssignmentStatus assignmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssignmentState state;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Integer version;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.assignedAt = (this.assignedAt == null) ? now : this.assignedAt;
        this.updatedAt = now;
        this.assignmentStatus = (this.assignmentStatus == null) ? AssignmentStatus.ASSIGNED : this.assignmentStatus;
        this.state = (this.state == null) ? AssignmentState.NOT_SUBMITTED : this.state;
        this.version = (this.version == null) ? 0 : this.version;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

