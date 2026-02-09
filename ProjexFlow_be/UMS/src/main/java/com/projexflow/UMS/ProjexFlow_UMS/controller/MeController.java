package com.projexflow.UMS.ProjexFlow_UMS.controller;

import com.projexflow.UMS.ProjexFlow_UMS.dto.AdminResponse;
import com.projexflow.UMS.ProjexFlow_UMS.dto.MentorResponse;
import com.projexflow.UMS.ProjexFlow_UMS.dto.StudentResponse;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Admin;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Mentor;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Role;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Student;
import com.projexflow.UMS.ProjexFlow_UMS.repository.AdminRepository;
import com.projexflow.UMS.ProjexFlow_UMS.repository.MentorRepository;
import com.projexflow.UMS.ProjexFlow_UMS.repository.StudentRepository;
import com.projexflow.UMS.ProjexFlow_UMS.security.AuthUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Frontend-friendly endpoint: returns the currently authenticated user's details.
 *
 * Usage:
 *   GET /api/v1/me
 *   Authorization: Bearer <jwt>
 *
 * If the token is invalid/expired -> 401 (handled by JwtAuthFilter).
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MeController {

    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final ModelMapper modelMapper;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal AuthUserPrincipal principal) {
        if (principal == null) {
            // In case the security context isn't populated for some reason
            return ResponseEntity.status(401).body(java.util.Map.of("message", "Unauthorized"));
        }

        Role role = Role.valueOf(principal.getRole());
        Long domainUserId = principal.getDomainUserId();

        return switch (role) {
            case ADMIN -> {
                Admin admin = adminRepository.findById(domainUserId)
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
                yield ResponseEntity.ok(modelMapper.map(admin, AdminResponse.class));
            }
            case STUDENT -> {
                Student student = studentRepository.findById(domainUserId)
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
                yield ResponseEntity.ok(modelMapper.map(student, StudentResponse.class));
            }
            case MENTOR -> {
                Mentor mentor = mentorRepository.findById(domainUserId)
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
                yield ResponseEntity.ok(modelMapper.map(mentor, MentorResponse.class));
            }
        };
    }
}
