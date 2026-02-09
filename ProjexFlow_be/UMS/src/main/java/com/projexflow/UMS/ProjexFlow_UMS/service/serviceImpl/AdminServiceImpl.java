package com.projexflow.UMS.ProjexFlow_UMS.service.serviceImpl;

import com.projexflow.UMS.ProjexFlow_UMS.dto.*;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Mentor;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Role;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Student;
import com.projexflow.UMS.ProjexFlow_UMS.security.AuthUser;
import com.projexflow.UMS.ProjexFlow_UMS.security.AuthUserRepository;
import com.projexflow.UMS.ProjexFlow_UMS.service.AdminService;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Admin;
import com.projexflow.UMS.ProjexFlow_UMS.exception.ConflictException;
import com.projexflow.UMS.ProjexFlow_UMS.exception.NotFoundException;
import com.projexflow.UMS.ProjexFlow_UMS.repository.AdminRepository;
import com.projexflow.UMS.ProjexFlow_UMS.service.MentorService;
import com.projexflow.UMS.ProjexFlow_UMS.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AuthUserRepository authUserRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentService studentService;
    private final MentorService mentorService;
    private final com.projexflow.UMS.ProjexFlow_UMS.repository.MentorRepository mentorRepository;
    private final com.projexflow.UMS.ProjexFlow_UMS.repository.StudentRepository studentRepository;


    /**
     * All-or-nothing:
     * If any record fails (duplicate email/prn/etc), transaction rolls back and nothing is created.
     */
    @Override
    @Transactional
    public List<StudentResponse> createStudentsBulk(List<StudentCreateRequest> requests) {
        List<StudentResponse> responses = new ArrayList<>();
        for (StudentCreateRequest req : requests) {
            responses.add(studentService.create(req));
        }
        return responses;
    }


    /**
     * All-or-nothing:
     * If any record fails, transaction rolls back and nothing is created.
     */
    @Override
    @Transactional
    public List<MentorResponse> createMentorsBulk(List<MentorCreateRequest> requests) {
        List<MentorResponse> responses = new ArrayList<>();
        for (MentorCreateRequest req : requests) {
            responses.add(mentorService.create(req));
        }
        return responses;
    }

    public AdminResponse create(AdminCreateRequest req) {
        if (authUserRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email is already in use");
        }
        Admin admin = new Admin();
        admin.setFullName(req.getFullName());
        admin.setEmail(req.getEmail());
        admin.setPassword(passwordEncoder.encode(req.getPassword()));
        admin.setProfilePhotoUrl(req.getProfilePhotoUrl());
        admin.setActive(req.isActive());
        admin.setDesignation(req.getDesignation());

        Admin saved = adminRepository.save(admin);
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

    public AdminResponse getById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Admin not found with id: " + id));
        return toResponse(admin);
    }

    public List<AdminResponse> getAll() {
        return adminRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public MentorResponse setMentorActive(Long mentorId, boolean active) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new NotFoundException("Mentor not found with id: " + mentorId));

        mentor.setActive(active);
        Mentor saved = mentorRepository.save(mentor);

        // Update AuthUser so authentication/authorization respects active status
        AuthUser auth = authUserRepository.findByRoleAndDomainUserId(Role.MENTOR, mentorId)
                .orElseThrow(() -> new NotFoundException("Auth user not found for mentorId: " + mentorId));
        auth.setActive(active);
        authUserRepository.save(auth);

        return new MentorResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getRole(),
                saved.getProfilePhotoUrl(),
                saved.isActive(),
                saved.getCourse(),
                saved.getEmpId(),
                saved.getDepartment(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    public StudentResponse setStudentActive(Long studentId, boolean active) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));

        student.setActive(active);
        Student saved = studentRepository.save(student);

        AuthUser auth = authUserRepository.findByRoleAndDomainUserId(Role.STUDENT, studentId)
                .orElseThrow(() -> new NotFoundException("Auth user not found for studentId: " + studentId));
        auth.setActive(active);
        authUserRepository.save(auth);

        return toResponse(saved);

    }

    public AdminResponse update(Long id, AdminUpdateRequest req) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Admin not found with id: " + id));

        if (req.getEmail() != null && !req.getEmail().equalsIgnoreCase(admin.getEmail())) {
            if (adminRepository.existsByEmail(req.getEmail())) {
                throw new ConflictException("Email is already in use");
            }
            admin.setEmail(req.getEmail());
        }

        if (req.getFullName() != null) admin.setFullName(req.getFullName());
        if (req.getProfilePhotoUrl() != null) admin.setProfilePhotoUrl(req.getProfilePhotoUrl());
        if (req.getDesignation() != null) admin.setDesignation(req.getDesignation());

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        Admin saved = adminRepository.save(admin);
        return toResponse(saved);
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
    private AdminResponse toResponse(Admin a) {
        return new AdminResponse(
                a.getId(),
                a.getFullName(),
                a.getEmail(),
                a.getRole(),
                a.getProfilePhotoUrl(),
                a.isActive(),
                a.getDesignation(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}
