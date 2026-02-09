package com.projexflow.mams.ProjexFlow_MAMS.service;

import com.projexflow.mams.ProjexFlow_MAMS.dto.AssignRequest;
import com.projexflow.mams.ProjexFlow_MAMS.dto.AssignResponse;
import com.projexflow.mams.ProjexFlow_MAMS.dto.AssignedGroupDetailsResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface MentorAssignmentService {
    @Transactional
    AssignResponse wipeAndAssign(AssignRequest req) ;
    List<Long> getAssignedGroupIds(Long mentorId, Long batchId) ;

    /** UI-friendly: return full group details for groups assigned to mentor in a batch. */
    List<AssignedGroupDetailsResponse> getAssignedGroupsDetailed(Long mentorId, Long batchId);

    /** Internal: find mentor assigned to a given group in a batch (if any). */
    com.projexflow.mams.ProjexFlow_MAMS.dto.MentorForGroupResponse getMentorForGroup(Long batchId, Long groupId);

    /** Dashboard metric: how many groups this mentor has in the given batch. */
    long countAssignedGroups(Long mentorId, Long batchId);
}

