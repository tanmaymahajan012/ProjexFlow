package com.projexflow.gms.ProjexFlow_GMS.dto;



import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActionResponse {
    private String message;   // "OK", "Already full", etc.
    private Long groupId;     // optional
}

