package com.projexflow.pms.ProjexFlow_PMS.dto;


import com.projexflow.pms.ProjexFlow_PMS.entity.ProjectStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AdminUpdateProjectRequest {
    private String title;
    private String description;
    private ProjectStatus status;
    private List<String> technologyStack;
    private LocalDate startDate;
    private LocalDate endDate;
    private String repoUrl;
    private String docsUrl;

    // for audit
    private String changesSummary;
}
