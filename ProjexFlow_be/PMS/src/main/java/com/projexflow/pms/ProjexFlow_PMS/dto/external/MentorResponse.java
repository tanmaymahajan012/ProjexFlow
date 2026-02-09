package com.projexflow.pms.ProjexFlow_PMS.dto.external;

import lombok.*;

/**
 * Mirror DTO for UMS internal mentor responses.
 * Keep fields minimal but UI-friendly.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MentorResponse {
    private Long id;
    private String name;
    private String email;
    private String photoUrl;
}
