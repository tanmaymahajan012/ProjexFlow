package com.projexflow.UMS.ProjexFlow_UMS.service.serviceImpl;


import com.projexflow.UMS.ProjexFlow_UMS.dto.StudentCreateRequest;
import com.projexflow.UMS.ProjexFlow_UMS.dto.StudentResponse;
import com.projexflow.UMS.ProjexFlow_UMS.dto.StudentUpdateRequest;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Student;
import com.projexflow.UMS.ProjexFlow_UMS.exception.BadRequestException;
import com.projexflow.UMS.ProjexFlow_UMS.exception.ConflictException;
import com.projexflow.UMS.ProjexFlow_UMS.exception.NotFoundException;
import com.projexflow.UMS.ProjexFlow_UMS.repository.StudentRepository;
import com.projexflow.UMS.ProjexFlow_UMS.security.AuthUser;
import com.projexflow.UMS.ProjexFlow_UMS.security.AuthUserRepository;
import com.projexflow.UMS.ProjexFlow_UMS.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final AuthUserRepository authUserRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;



    public StudentResponse create(StudentCreateRequest req) {
        if (authUserRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email is already in use");
        }
        if (studentRepository.existsByPrn(req.getPrn())) {
            throw new ConflictException("PRN is already in use");
        }

        Student student = new Student();
        student.setFullName(req.getFullName());
        student.setEmail(req.getEmail());
        student.setPassword(passwordEncoder.encode(req.getPassword()));
        student.setProfilePhotoUrl(req.getProfilePhotoUrl());
        student.setActive(req.isActive());
        student.setRollNo(req.getRollNo());
        student.setPrn(req.getPrn());
        student.setGithubUrl(req.getGithubUrl());
        student.setCourse(req.getCourse());
        student.setBatchId(req.getBatchId());

        Student saved = studentRepository.save(student);
        authUserRepository.save(
                AuthUser.builder()
                        .email(saved.getEmail())
                        .passwordHash(saved.getPassword()) // already hashed
                        .role(saved.getRole())             // STUDENT
                        .domainUserId(saved.getId())
                        .active(saved.isActive())
                        .build()
        );
        return toResponse(saved);
    }

    @Override
    public List<Long> getAvailableBatchIds() {
        return studentRepository.findDistinctActiveBatchIds();
    }

    @Override
    public List<String> getAvailableCourses() {
        return studentRepository.findDistinctActiveCourses();
    }
    @Override
    public StudentResponse getById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + id));
        return toResponse(student);
    }

    public List<StudentResponse> getAll() {
        return studentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public StudentResponse update(Long id, StudentUpdateRequest req) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + id));

        if (req.getEmail() != null && !req.getEmail().equalsIgnoreCase(student.getEmail())) {
            if (studentRepository.existsByEmail(req.getEmail())) {
                throw new ConflictException("Email is already in use");
            }
            student.setEmail(req.getEmail());
        }

        if (req.getPrn() != null && !req.getPrn().equalsIgnoreCase(student.getPrn())) {
            if (studentRepository.existsByPrn(req.getPrn())) {
                throw new ConflictException("PRN is already in use");
            }
            student.setPrn(req.getPrn());
        }

        if (req.getFullName() != null) student.setFullName(req.getFullName());
        if (req.getProfilePhotoUrl() != null) student.setProfilePhotoUrl(req.getProfilePhotoUrl());
        if (req.getRollNo() != null) student.setRollNo(req.getRollNo());
        if (req.getGithubUrl() != null) student.setGithubUrl(req.getGithubUrl());
        if (req.getCourse() != null) student.setCourse(req.getCourse());
        if (req.getBatchId() != null) student.setBatchId(req.getBatchId());

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            student.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        Student saved = studentRepository.save(student);
        return toResponse(saved);
    }

    @Override
    public StudentResponse updateProfilePhotoUrl(Long id, String profilePhotoUrl) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + id));
        student.setProfilePhotoUrl(profilePhotoUrl);
        Student saved = studentRepository.save(student);
        return toResponse(saved);
    }

    @Override
    public List<StudentResponse> getByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return studentRepository.findAllById(ids).stream().map(this::toResponse).toList();
    }

    @Override
    public StudentResponse getByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Student not found with email: " + email));
        return toResponse(student);
    }
    @Override
    public StudentResponse getByPrn(String prn) {
        Student student = studentRepository.findByPrn(prn)
                .orElseThrow(() -> new RuntimeException("Student not found with PRN: " + prn));
        return mapToResponse(student);
    }

    @Override
    public List<StudentResponse> getByBatchIdAndCourse(Long batchId, String course) {
        List<Student> students = studentRepository.findByBatchIdAndCourse(batchId, course);
        return students.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<StudentResponse> getByBatchId(Long batchId) {
        List<Student> students = studentRepository.findByBatchId(batchId);
        return students.stream()
                .map(this::mapToResponse)
                .toList();
    }
    private StudentResponse mapToResponse(Student student) {
        return modelMapper.map(student, StudentResponse.class);
    }

    private Student mapToEntity(StudentCreateRequest request) {
        return modelMapper.map(request, Student.class);
    }



    private StudentResponse toResponse(Student s) {
        return new StudentResponse(
                s.getId(),
                s.getFullName(),
                s.getEmail(),
                s.getRole(),
                s.getProfilePhotoUrl(),
                s.isActive(),
                s.getRollNo(),
                s.getPrn(),
                s.getGithubUrl(),
                s.getCourse(),
                s.getBatchId(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }

}
