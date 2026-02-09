package com.projexflow.UMS.ProjexFlow_UMS.controller;

import com.projexflow.UMS.ProjexFlow_UMS.dto.StudentCreateRequest;
import com.projexflow.UMS.ProjexFlow_UMS.dto.StudentResponse;
import com.projexflow.UMS.ProjexFlow_UMS.dto.StudentUpdateRequest;
import com.projexflow.UMS.ProjexFlow_UMS.service.CloudinaryUploadService;
import com.projexflow.UMS.ProjexFlow_UMS.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final CloudinaryUploadService uploadService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@Valid @RequestBody StudentCreateRequest request) {
        return studentService.create(request);
    }

    @GetMapping("/batch-ids")
    public List<Long> getAvailableBatchIds() {
        return studentService.getAvailableBatchIds();
    }

    @GetMapping("/courses")
    public List<String> getAvailableCourses() {
        return studentService.getAvailableCourses();
    }

    @GetMapping("/{id}")
    public StudentResponse getById(@PathVariable Long id) {
        return studentService.getById(id);
    }

    @GetMapping
    public List<StudentResponse> getAll() {
        return studentService.getAll();
    }
    @PatchMapping("/{id}")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentUpdateRequest request) {
        return studentService.update(id, request);
    }

    /**
     * Uploads profile photo to Cloudinary and persists URL in UMS.
     * UI can directly render profilePhotoUrl from the response.
     */
    @PutMapping(value = "/{id}/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StudentResponse uploadProfilePhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        String url = uploadService.uploadProfilePhoto(file, id);
        return studentService.updateProfilePhotoUrl(id, url);
    }
    @GetMapping("/prn/{prn}")
    public StudentResponse getByPrn(@PathVariable String prn) {
        return studentService.getByPrn(prn);
    }

    @GetMapping("/search")
    public List<StudentResponse> getByBatchIdAndCourse(
            @RequestParam Long batchId,
            @RequestParam String course
    ) {
        return studentService.getByBatchIdAndCourse(batchId, course);
    }

}
