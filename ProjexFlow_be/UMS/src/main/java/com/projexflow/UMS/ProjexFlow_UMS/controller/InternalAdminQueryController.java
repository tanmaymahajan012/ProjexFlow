package com.projexflow.UMS.ProjexFlow_UMS.controller;

import com.projexflow.UMS.ProjexFlow_UMS.dto.IdOnlyResponse;
import com.projexflow.UMS.ProjexFlow_UMS.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Internal endpoints used by other services to resolve admin IDs by email.
 */
@RestController
@RequestMapping("/api/v1/internal/admins")
@RequiredArgsConstructor
public class InternalAdminQueryController {

    private final AdminRepository adminRepository;

    @GetMapping("/by-email")
    public IdOnlyResponse adminByEmail(@RequestParam("email") String email) {
        Long id = adminRepository.findByEmail(email)
                .map(a -> a.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found: " + email));
        return new IdOnlyResponse(id);
    }
}
