package com.projexflow.UMS.ProjexFlow_UMS.controller;

import com.projexflow.UMS.ProjexFlow_UMS.dto.MentorCreateRequest;
import com.projexflow.UMS.ProjexFlow_UMS.dto.MentorResponse;
import com.projexflow.UMS.ProjexFlow_UMS.dto.MentorUpdateRequest;
import com.projexflow.UMS.ProjexFlow_UMS.service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mentors")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentorResponse create(@Valid @RequestBody MentorCreateRequest request) {
        return mentorService.create(request);
    }

    @GetMapping("/{id}")
    public MentorResponse getById(@PathVariable Long id) {
        return mentorService.getById(id);
    }

    @GetMapping
    public List<MentorResponse> getAll() {
        return mentorService.getAll();
    }
    @PatchMapping("/{id}")
    public MentorResponse update(@PathVariable Long id, @Valid @RequestBody MentorUpdateRequest request) {
        return mentorService.update(id, request);
    }


}