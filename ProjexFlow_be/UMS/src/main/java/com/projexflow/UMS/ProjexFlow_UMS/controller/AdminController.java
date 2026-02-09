package com.projexflow.UMS.ProjexFlow_UMS.controller;

import com.projexflow.UMS.ProjexFlow_UMS.dto.*;
import com.projexflow.UMS.ProjexFlow_UMS.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/students/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<StudentResponse> createStudentsBulk(
            @Valid @RequestBody List<@Valid StudentCreateRequest> requests
    ) {
        return adminService.createStudentsBulk(requests);
    }
    @PatchMapping("/mentors/{mentorId}/active")
    public MentorResponse setMentorActive(
            @PathVariable Long mentorId,
            @Valid @RequestBody ActiveStatusUpdateRequest req
    ) {
        return adminService.setMentorActive(mentorId, req.getActive());
    }

    @PatchMapping("/students/{studentId}/active")
    public StudentResponse setStudentActive(
            @PathVariable Long studentId,
            @Valid @RequestBody ActiveStatusUpdateRequest req
    ) {
        return adminService.setStudentActive(studentId, req.getActive());
    }

    @PostMapping("/mentors/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<MentorResponse> createMentorsBulk(
            @Valid @RequestBody List<@Valid MentorCreateRequest> requests
    ) {
        return adminService.createMentorsBulk(requests);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminResponse create(@Valid @RequestBody AdminCreateRequest request) {
        return adminService.create(request);
    }

    @GetMapping("/{id}")
    public AdminResponse getById(@PathVariable Long id) {
        return adminService.getById(id);
    }

    @GetMapping
    public List<AdminResponse> getAll() {
        return adminService.getAll();
    }
    @PatchMapping("/{id}")
    public AdminResponse update(@PathVariable Long id, @Valid @RequestBody AdminUpdateRequest request) {
        return adminService.update(id, request);
    }

}

