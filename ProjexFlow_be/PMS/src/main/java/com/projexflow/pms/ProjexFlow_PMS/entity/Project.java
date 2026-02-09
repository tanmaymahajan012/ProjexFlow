package com.projexflow.pms.ProjexFlow_PMS.entity;



import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document("projects")
@CompoundIndex(name = "uniq_batch_group", def = "{'batchId': 1, 'groupId': 1}", unique = true)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Project {

    @Id
    private String id;

    private Long batchId;
    private Long groupId;

    private Long createdByStudentId;

    private String title;
    private String description;

    private ProjectStatus status;

    private List<String> technologyStack = new ArrayList<>();

    private LocalDate startDate;
    private LocalDate endDate;

    private String repoUrl;
    private String docsUrl;

    @Builder.Default
    private boolean lockedAfterCreate = true;

    @Builder.Default
    private List<AdminEditAudit> adminEdits = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class AdminEditAudit {
        private Long adminId;
        private Instant editedAt;
        private String changesSummary;
    }
}
