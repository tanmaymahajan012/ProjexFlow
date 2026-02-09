package com.projexflow.pms.ProjexFlow_PMS.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CreateProjectRequest {

    @NotNull
    private Long batchId;

    private Long groupId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotEmpty
    private List<@NotBlank String> technologyStack;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String repoUrl;
    private String docsUrl;
}
