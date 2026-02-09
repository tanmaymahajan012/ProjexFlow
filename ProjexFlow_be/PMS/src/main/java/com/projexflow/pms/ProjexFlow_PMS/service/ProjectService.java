package com.projexflow.pms.ProjexFlow_PMS.service;

import com.projexflow.pms.ProjexFlow_PMS.dto.AdminUpdateProjectRequest;
import com.projexflow.pms.ProjexFlow_PMS.dto.CreateProjectRequest;
import com.projexflow.pms.ProjexFlow_PMS.dto.ProjectResponse;

import java.util.List;

public interface ProjectService {
    ProjectResponse createProject(Long studentId, String role, CreateProjectRequest req);
    ProjectResponse getMyProject(Long studentId, Long batchId) ;

    ProjectResponse getByBatchAndGroup(Long requesterId, String role, Long batchId, Long groupId) ;

    ProjectResponse adminUpdate(String projectId, Long adminId, AdminUpdateProjectRequest req) ;

    /** UI-friendly: list projects for groups assigned to mentor in a batch. */
    List<ProjectResponse> listForMentor(Long mentorId, Long batchId);

    /** UI-friendly: list all projects in a batch for ADMIN dashboards. */
    List<ProjectResponse> listForAdminBatch(Long batchId);
}

