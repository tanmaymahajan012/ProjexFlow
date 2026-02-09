package com.projexflow.gms.ProjexFlow_GMS.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * UI-friendly request body for sending a group invite.
 *
 * The frontend only sends the recipient email.
 * - batchId is derived from the authenticated student (X-EMAIL -> UMS -> batchId)
 * - recipient studentId is resolved by email (UMS)
 */
@Getter
@Setter
public class SendInviteDto {
    @NotBlank
    @Email
    private String toEmail;
}
