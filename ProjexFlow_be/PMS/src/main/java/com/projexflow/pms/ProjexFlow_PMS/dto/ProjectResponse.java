package com.projexflow.pms.ProjexFlow_PMS.dto;


import com.projexflow.pms.ProjexFlow_PMS.entity.ProjectStatus;
import com.projexflow.pms.ProjexFlow_PMS.dto.external.GroupDetailsResponse;
import com.projexflow.pms.ProjexFlow_PMS.dto.external.MentorResponse;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProjectResponse {
    private String id;
    private Long batchId;
    private Long groupId;

    // UI-friendly: includes members and student profiles (from GMS)
    private GroupDetailsResponse groupDetails;

    // UI-friendly: mentor details (resolved via MAMS -> UMS)
    private MentorResponse mentor;

    private String title;
    private String description;
    private ProjectStatus status;
    private List<String> technologyStack;

    private LocalDate startDate;
    private LocalDate endDate;

    private String repoUrl;
    private String docsUrl;

    private Instant createdAt;
    private Instant updatedAt;
}