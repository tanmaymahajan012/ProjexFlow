package com.projexflow.gms.ProjexFlow_GMS.service;

import com.projexflow.gms.ProjexFlow_GMS.dto.ActionResponse;
import com.projexflow.gms.ProjexFlow_GMS.dto.IncomingGroupRequestResponse;
import com.projexflow.gms.ProjexFlow_GMS.dto.SendRequestDto;
import com.projexflow.gms.ProjexFlow_GMS.dto.SentGroupRequestResponse;
import com.projexflow.gms.ProjexFlow_GMS.entity.Group;
import com.projexflow.gms.ProjexFlow_GMS.entity.GroupJoinRequest;
import com.projexflow.gms.ProjexFlow_GMS.entity.GroupMember;
import com.projexflow.gms.ProjexFlow_GMS.entity.RequestStatus;
import com.projexflow.gms.ProjexFlow_GMS.exception.NotFoundException;
import com.projexflow.gms.ProjexFlow_GMS.repository.GroupJoinRequestRepo;
import com.projexflow.gms.ProjexFlow_GMS.repository.GroupMemberRepo;
import com.projexflow.gms.ProjexFlow_GMS.repository.GroupRepo;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

public interface GroupManagementService {
    @Transactional
    ActionResponse accept(Long requestId, Long currentStudentId);

    Long sendRequest(Long fromStudentId, SendRequestDto dto);

    @Transactional
    ActionResponse reject(Long requestId, Long currentStudentId);

    List<IncomingGroupRequestResponse> getIncomingRequests(Long batchId, String course, Long studentId);

    List<SentGroupRequestResponse> getSentRequests(Long batchId, String course, Long studentId);

    /**
     * UI-friendly group details: includes full member profiles (via UMS).
     */
    com.projexflow.gms.ProjexFlow_GMS.dto.GroupDetailsResponse getGroupDetails(Long groupId);

    /**
     * UI-friendly: list all groups in a batch including full member profiles (via
     * UMS).
     */
    java.util.List<com.projexflow.gms.ProjexFlow_GMS.dto.GroupDetailsResponse> getBatchGroups(Long batchId);

    /** UI-friendly: list all groups in a batch for a specific course. */
    java.util.List<com.projexflow.gms.ProjexFlow_GMS.dto.GroupDetailsResponse> getBatchGroups(Long batchId,
            String course);

    /**
     * UI-friendly: current student's active group (if any) for a batch.
     */
    com.projexflow.gms.ProjexFlow_GMS.dto.MyGroupResponse getMyGroup(Long batchId, Long studentId);

    /**
     * Dashboard metric: active member count for the current student's group in a
     * batch.
     */
    long countMyGroupMembers(Long batchId, Long studentId);

}
