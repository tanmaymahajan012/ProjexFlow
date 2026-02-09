package com.projexflow.pms.ProjexFlow_PMS.service.serviceImpl;



import com.projexflow.pms.ProjexFlow_PMS.dto.AdminUpdateProjectRequest;
import com.projexflow.pms.ProjexFlow_PMS.dto.CreateProjectRequest;
import com.projexflow.pms.ProjexFlow_PMS.dto.ProjectResponse;
import com.projexflow.pms.ProjexFlow_PMS.entity.Project;
import com.projexflow.pms.ProjexFlow_PMS.entity.ProjectStatus;
import com.projexflow.pms.ProjexFlow_PMS.exception.AccessDeniedException;
import com.projexflow.pms.ProjexFlow_PMS.exception.ApiException;
import com.projexflow.pms.ProjexFlow_PMS.repository.ProjectRepository;
import com.projexflow.pms.ProjexFlow_PMS.service.GmsClient;
import com.projexflow.pms.ProjexFlow_PMS.service.MamsClient;
import com.projexflow.pms.ProjexFlow_PMS.service.ProjectService;
import com.projexflow.pms.ProjexFlow_PMS.service.UmsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository repo;
    private final GmsClient gms;
    private final MamsClient mams;
    private final UmsClient ums;

    public ProjectResponse createProject(Long requesterId, String role, CreateProjectRequest req) {
        if (!"STUDENT".equals(role) && !"ADMIN".equals(role)) {
            throw new AccessDeniedException("Only STUDENT or ADMIN can create a project");
        }

        // enforce grouping closed
        String status = gms.groupingStatus(req.getBatchId());
        if (!"CLOSED".equalsIgnoreCase(status)) {
            throw new ApiException("Grouping is not closed yet. Project creation is allowed only after grouping is CLOSED.");
        }

        Long groupId;
        if ("STUDENT".equals(role)) {
            // Student must NOT pass groupId from UI; resolve from JWT -> studentId -> active group
            groupId = gms.getMyGroupId(req.getBatchId(), requesterId);
            if (groupId == null) {
                throw new ApiException("No active group found for this batch");
            }
        } else {
            // ADMIN creates for a specific group
            if (req.getGroupId() == null) {
                throw new ApiException("groupId is required for ADMIN project creation");
            }
            groupId = req.getGroupId();
        }

        // verify active membership for STUDENT (defense-in-depth)
        if ("STUDENT".equals(role)) {
            Boolean member = gms.isMember(req.getBatchId(), groupId, requesterId);
            if (member == null || !member) throw new AccessDeniedException("You are not an active member of this group");
        }

        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new ApiException("endDate cannot be before startDate");
        }

        Project p = Project.builder()
                .batchId(req.getBatchId())
                .groupId(groupId)
                .createdByStudentId("STUDENT".equals(role) ? requesterId : null)
                .title(req.getTitle())
                .description(req.getDescription())
                .technologyStack(req.getTechnologyStack())
                .status(ProjectStatus.PROPOSED)
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .repoUrl(req.getRepoUrl())
                .docsUrl(req.getDocsUrl())
                .lockedAfterCreate(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Project saved = repo.save(p);
        return toResponse(saved);
    }

    public ProjectResponse getMyProject(Long studentId, Long batchId) {
        Long groupId = gms.getMyGroupId(batchId, studentId);
        if (groupId == null) throw new ApiException("No group found for this batch");

        Project p = repo.findByBatchIdAndGroupId(batchId, groupId)
                .orElseThrow(() -> new ApiException("Project not created yet for your group"));

        // verify active membership at read time too
        Boolean member = gms.isMember(batchId, groupId, studentId);
        if (member == null || !member) throw new AccessDeniedException("You are not an active member of this group");

        return toResponse(p);
    }

    public ProjectResponse getByBatchAndGroup(Long requesterId, String role, Long batchId, Long groupId) {
        Project p = repo.findByBatchIdAndGroupId(batchId, groupId)
                .orElseThrow(() -> new ApiException("Project not found"));

        // Access rules:
        // ADMIN always ok
        // STUDENT only if active member
        // MENTOR only if this group is assigned to the mentor (via MAMS)
        if ("ADMIN".equals(role)) return toResponse(p);

        if ("STUDENT".equals(role)) {
            Boolean member = gms.isMember(batchId, groupId, requesterId);
            if (member == null || !member) throw new AccessDeniedException("Not allowed");
            return toResponse(p);
        }

        if ("MENTOR".equals(role)) {
            List<Long> groups = mams.getAssignedGroupIds(requesterId, batchId);
            if (groups != null && groups.contains(groupId)) {
                return toResponse(p);
            }
            throw new AccessDeniedException("Not allowed");
        }

        throw new AccessDeniedException("Not allowed");
    }

    public ProjectResponse adminUpdate(String projectId, Long adminId, AdminUpdateProjectRequest req) {
        Project p = repo.findById(projectId).orElseThrow(() -> new ApiException("Project not found"));

        // Only ADMIN should reach here (enforced by controller @PreAuthorize)
        if (req.getTitle() != null) p.setTitle(req.getTitle());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getStatus() != null) p.setStatus(req.getStatus());
        if (req.getTechnologyStack() != null && !req.getTechnologyStack().isEmpty()) p.setTechnologyStack(req.getTechnologyStack());
        if (req.getStartDate() != null) p.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) p.setEndDate(req.getEndDate());
        if (req.getRepoUrl() != null) p.setRepoUrl(req.getRepoUrl());
        if (req.getDocsUrl() != null) p.setDocsUrl(req.getDocsUrl());

        if (p.getEndDate() != null && p.getStartDate() != null && p.getEndDate().isBefore(p.getStartDate())) {
            throw new ApiException("endDate cannot be before startDate");
        }

        p.getAdminEdits().add(Project.AdminEditAudit.builder()
                .adminId(adminId)
                .editedAt(Instant.now())
                .changesSummary(Optional.ofNullable(req.getChangesSummary()).orElse("Admin update"))
                .build());

        p.setUpdatedAt(Instant.now());
        Project saved = repo.save(p);
        return toResponse(saved);
    }

    
@Override
public List<ProjectResponse> listForMentor(Long mentorId, Long batchId) {
    List<Long> groupIds = mams.getAssignedGroupIds(mentorId, batchId);
    if (groupIds == null || groupIds.isEmpty()) return List.of();

    return repo.findAllByBatchIdAndGroupIdIn(batchId, groupIds)
            .stream()
            .map(this::toResponse)
            .toList();
}


@Override
public List<ProjectResponse> listForAdminBatch(Long batchId) {
    return repo.findAllByBatchId(batchId)
            .stream()
            .map(this::toResponse)
            .toList();
}

private ProjectResponse toResponse(Project p) {
        var groupDetails = gms.getGroupDetails(p.getGroupId());

        // Best-effort mentor enrichment (UI-friendly). If not assigned or downstream is unavailable, mentor stays null.
        com.projexflow.pms.ProjexFlow_PMS.dto.external.MentorResponse mentor = null;
        try {
            var mfg = mams.getMentorForGroup(p.getBatchId(), p.getGroupId());
            if (mfg != null && mfg.getMentorId() != null) {
                var mentors = ums.getMentorsByIds(java.util.List.of(mfg.getMentorId()));
                if (mentors != null && !mentors.isEmpty()) {
                    mentor = mentors.get(0);
                }
            }
        } catch (Exception ignored) { }

        return ProjectResponse.builder()
                .id(p.getId())
                .batchId(p.getBatchId())
                .groupId(p.getGroupId())
                .groupDetails(groupDetails)
                .mentor(mentor)
                .title(p.getTitle())
                .description(p.getDescription())
                .status(p.getStatus())
                .technologyStack(p.getTechnologyStack())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .repoUrl(p.getRepoUrl())
                .docsUrl(p.getDocsUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}

