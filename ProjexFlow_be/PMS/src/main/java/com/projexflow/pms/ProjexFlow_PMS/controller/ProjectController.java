package com.projexflow.pms.ProjexFlow_PMS.controller;



import com.projexflow.pms.ProjexFlow_PMS.dto.AdminUpdateProjectRequest;
import com.projexflow.pms.ProjexFlow_PMS.dto.CreateProjectRequest;
import com.projexflow.pms.ProjexFlow_PMS.dto.ProjectResponse;
import com.projexflow.pms.ProjexFlow_PMS.identity.CurrentUserResolver;
import com.projexflow.pms.ProjexFlow_PMS.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pms/projects")
@RequiredArgsConstructor
public class ProjectController{
    private final ProjectService service;
    private final CurrentUserResolver currentUser;

    @PostMapping
    public ProjectResponse create(@RequestBody CreateProjectRequest req) {
        String role = currentUser.role();
        Long roleId = currentUser.roleSpecificId();
        return service.createProject(roleId, role, req);
    }

    @GetMapping("/my")
    public ProjectResponse my() {
        String role = currentUser.role();
        if (!"STUDENT".equals(role)) {
            throw new com.projexflow.pms.ProjexFlow_PMS.exception.AccessDeniedException("Only STUDENT can access /my");
        }
        var student = currentUser.student();
        return service.getMyProject(student.getId(), student.getBatchId());
    }

    @GetMapping
    public ProjectResponse getByBatchAndGroup(@RequestParam Long batchId,
                                              @RequestParam Long groupId) {
        String role = currentUser.role();
        Long requesterId = currentUser.roleSpecificId();
        return service.getByBatchAndGroup(requesterId, role, batchId, groupId);
    }


@GetMapping("/mentor")
public java.util.List<ProjectResponse> mentorProjects(@RequestParam Long batchId) {
    String role = currentUser.role();
    if (!"MENTOR".equals(role)) {
        throw new com.projexflow.pms.ProjexFlow_PMS.exception.AccessDeniedException("Only MENTOR can access /mentor");
    }
    Long mentorId = currentUser.mentorId();
    return service.listForMentor(mentorId, batchId);
}



@GetMapping("/admin/batch/{batchId}")
public java.util.List<ProjectResponse> adminBatch(@PathVariable Long batchId) {
    String role = currentUser.role();
    if (!"ADMIN".equals(role)) {
        throw new com.projexflow.pms.ProjexFlow_PMS.exception.AccessDeniedException("Only ADMIN can access /admin/batch/{batchId}");
    }
    return service.listForAdminBatch(batchId);
}

    @PatchMapping("/{projectId}")
    public ProjectResponse adminUpdate(@PathVariable String projectId,
                                       @RequestBody AdminUpdateProjectRequest req) {
        String role = currentUser.role();
        if (!"ADMIN".equals(role)) {
            throw new com.projexflow.pms.ProjexFlow_PMS.exception.AccessDeniedException("Only ADMIN can update a project");
        }
        Long adminId = currentUser.adminId();
        return service.adminUpdate(projectId, adminId, req);
    }

}

