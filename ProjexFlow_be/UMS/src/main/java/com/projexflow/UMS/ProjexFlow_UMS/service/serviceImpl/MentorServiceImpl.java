package com.projexflow.UMS.ProjexFlow_UMS.service.serviceImpl;

import com.projexflow.UMS.ProjexFlow_UMS.dto.MentorUpdateRequest;
import com.projexflow.UMS.ProjexFlow_UMS.security.AuthUser;
import com.projexflow.UMS.ProjexFlow_UMS.security.AuthUserRepository;
import com.projexflow.UMS.ProjexFlow_UMS.service.MentorService;
import com.projexflow.UMS.ProjexFlow_UMS.dto.MentorCreateRequest;
import com.projexflow.UMS.ProjexFlow_UMS.dto.MentorResponse;
import com.projexflow.UMS.ProjexFlow_UMS.entity.Mentor;
import com.projexflow.UMS.ProjexFlow_UMS.exception.ConflictException;
import com.projexflow.UMS.ProjexFlow_UMS.exception.NotFoundException;
import com.projexflow.UMS.ProjexFlow_UMS.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {
    private final AuthUserRepository authUserRepository;
    private final MentorRepository mentorRepository;
    private final PasswordEncoder passwordEncoder;

    public MentorResponse create(MentorCreateRequest req) {
        if (authUserRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email is already in use");
        }
        if (mentorRepository.existsByEmpId(req.getEmpId())) {
            throw new ConflictException("Employee ID is already in use");
        }

        Mentor mentor = new Mentor();
        mentor.setFullName(req.getFullName());
        mentor.setEmail(req.getEmail());
        mentor.setPassword(passwordEncoder.encode(req.getPassword()));
        mentor.setProfilePhotoUrl(req.getProfilePhotoUrl());
        mentor.setActive(req.isActive());
        mentor.setCourse(req.getCourse());
        mentor.setEmpId(req.getEmpId());
        mentor.setDepartment(req.getDepartment());

        Mentor saved = mentorRepository.save(mentor);
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

    public MentorResponse getById(Long id) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mentor not found with id: " + id));
        return toResponse(mentor);
    }

    public List<MentorResponse> getAll() {
        return mentorRepository.findAll().stream().map(this::toResponse).toList();
    }
    public MentorResponse update(Long id, MentorUpdateRequest req) {
        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mentor not found with id: " + id));

        if (req.getEmail() != null && !req.getEmail().equalsIgnoreCase(mentor.getEmail())) {
            if (mentorRepository.existsByEmail(req.getEmail())) {
                throw new ConflictException("Email is already in use");
            }
            mentor.setEmail(req.getEmail());
        }

        if (req.getEmpId() != null && !req.getEmpId().equalsIgnoreCase(mentor.getEmpId())) {
            if (mentorRepository.existsByEmpId(req.getEmpId())) {
                throw new ConflictException("Employee ID is already in use");
            }
            mentor.setEmpId(req.getEmpId());
        }

        if (req.getFullName() != null) mentor.setFullName(req.getFullName());
        if (req.getProfilePhotoUrl() != null) mentor.setProfilePhotoUrl(req.getProfilePhotoUrl());
        if (req.getCourse() != null) mentor.setCourse(req.getCourse());
        if (req.getDepartment() != null) mentor.setDepartment(req.getDepartment());

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            mentor.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        Mentor saved = mentorRepository.save(mentor);
        return toResponse(saved);
    }


    private MentorResponse toResponse(Mentor m) {
        return new MentorResponse(
                m.getId(),
                m.getFullName(),
                m.getEmail(),
                m.getRole(),
                m.getProfilePhotoUrl(),
                m.isActive(),
                m.getCourse(),
                m.getEmpId(),
                m.getDepartment(),
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }
}
