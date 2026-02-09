package com.projexflow.gms.ProjexFlow_GMS.controller;

import com.projexflow.gms.ProjexFlow_GMS.dto.*;
import com.projexflow.gms.ProjexFlow_GMS.dto.external.UmsStudentResponse;
import com.projexflow.gms.ProjexFlow_GMS.exception.BadRequestException;
import com.projexflow.gms.ProjexFlow_GMS.exception.CourseMismatchException;
import com.projexflow.gms.ProjexFlow_GMS.exception.NotFoundException;
import com.projexflow.gms.ProjexFlow_GMS.service.GroupManagementService;
import com.projexflow.gms.ProjexFlow_GMS.service.UmsClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/gms")
@RequiredArgsConstructor
public class GroupManagementController {

    @Autowired
    private final GroupManagementService service;
    private final UmsClient umsClient;

    private UmsStudentResponse resolveStudent(String email) {
        return umsClient.getStudentByEmail(email);
    }

    private Long resolveStudentId(String email) {
        UmsStudentResponse s = resolveStudent(email);
        return s == null ? null : s.getId();
    }

    @PostMapping("/requests")
    public Map<String, Object> sendRequest(
            // Always resolve studentId from the JWT subject/email (forwarded by the API
            // gateway).
            // This prevents accidentally persisting auth-user IDs into columns like
            // from_student_id.
            @RequestHeader("X-EMAIL") String email,
            @RequestBody @Valid SendInviteDto dto) {
        UmsStudentResponse from = resolveStudent(email);
        if (from == null)
            throw new NotFoundException("Authenticated student not found");

        UmsStudentResponse to = resolveStudent(dto.getToEmail());
        if (to == null)
            throw new NotFoundException("Recipient student not found for email: " + dto.getToEmail());

        // Enforce rule: friend/group requests can be sent ONLY within same batch and
        // same course.
        if (from.getBatchId() == null || to.getBatchId() == null || !from.getBatchId().equals(to.getBatchId())) {
            throw new BadRequestException("Recipient must be in the same batch");
        }
        String fromCourse = from.getCourse();
        String toCourse = to.getCourse();
        if (fromCourse == null || toCourse == null || !fromCourse.equalsIgnoreCase(toCourse)) {
            throw new CourseMismatchException("Recipient must be in the same course");
        }

        // Service-level DTO requires batchId + toStudentId.
        SendRequestDto legacy = new SendRequestDto();
        legacy.setBatchId(from.getBatchId());
        legacy.setCourse(from.getCourse());
        legacy.setToStudentId(to.getId());

        Long requestId = service.sendRequest(from.getId(), legacy);
        return Map.of("requestId", requestId);
    }

    @PostMapping("/requests/{requestId}/accept")
    public ActionResponse accept(
            @PathVariable Long requestId,
            @RequestHeader("X-EMAIL") String email) {
        return service.accept(requestId, resolveStudentId(email));
    }

    @PostMapping("/requests/{requestId}/reject")
    public ActionResponse reject(
            @PathVariable Long requestId,
            @RequestHeader("X-EMAIL") String email) {
        return service.reject(requestId, resolveStudentId(email));
    }

    /**
     * UI-friendly group endpoint: returns full member information (not just student
     * IDs).
     */
    @GetMapping("/groups/{groupId}")
    public GroupDetailsResponse getGroupDetails(@PathVariable Long groupId) {
        return service.getGroupDetails(groupId);
    }

    /**
     * UI-friendly: list all groups for a batch with full member profiles.
     */
    @GetMapping("/batches/{batchId}/groups")
    public List<GroupDetailsResponse> listBatchGroups(@PathVariable Long batchId,
            @RequestParam(name = "course", required = false) String course) {
        return (course == null || course.isBlank()) ? service.getBatchGroups(batchId)
                : service.getBatchGroups(batchId, course);
    }

    /**
     * UI-friendly: current student's active group details (if any) for a batch.
     */
    @GetMapping("/batches/{batchId}/me/group")
    public MyGroupResponse myGroup(
            @PathVariable Long batchId,
            @RequestHeader("X-EMAIL") String email) {
        return service.getMyGroup(batchId, resolveStudentId(email));
    }

    /**
     * Student dashboard metric: number of active members in the current student's
     * group for a batch.
     */
    @GetMapping("/batches/{batchId}/me/group/members/count")
    public CountResponse myGroupMembersCount(
            @PathVariable Long batchId,
            @RequestHeader("X-EMAIL") String email) {
        Long studentId = resolveStudentId(email);
        if (studentId == null)
            throw new NotFoundException("Authenticated student not found");
        long count = service.countMyGroupMembers(batchId, studentId);
        return new CountResponse(count);
    }

    @GetMapping("/batches/{batchId}/requests/incoming")
    public List<IncomingGroupRequestResponse> incomingRequests(
            @PathVariable Long batchId,
            @RequestHeader("X-EMAIL") String email) {
        UmsStudentResponse me = resolveStudent(email);
        if (me == null)
            throw new NotFoundException("Authenticated student not found");
        return service.getIncomingRequests(batchId, me.getCourse(), me.getId());
    }

    @GetMapping("/batches/{batchId}/requests/sent")
    public List<SentGroupRequestResponse> sentRequests(
            @PathVariable Long batchId,
            @RequestHeader("X-EMAIL") String email) {
        UmsStudentResponse me = resolveStudent(email);
        if (me == null)
            throw new NotFoundException("Authenticated student not found");
        return service.getSentRequests(batchId, me.getCourse(), me.getId());
    }

}
