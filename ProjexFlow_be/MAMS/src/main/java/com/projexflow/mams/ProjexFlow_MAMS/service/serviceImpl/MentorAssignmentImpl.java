package com.projexflow.mams.ProjexFlow_MAMS.service.serviceImpl;

import com.projexflow.mams.ProjexFlow_MAMS.dto.AssignRequest;
import com.projexflow.mams.ProjexFlow_MAMS.dto.AssignResponse;
import com.projexflow.mams.ProjexFlow_MAMS.dto.AssignedGroupDetailsResponse;
import com.projexflow.mams.ProjexFlow_MAMS.dto.MentorForGroupResponse;
import com.projexflow.mams.ProjexFlow_MAMS.entity.MentorGroup;
import com.projexflow.mams.ProjexFlow_MAMS.repository.MentorGroupRepository;
import com.projexflow.mams.ProjexFlow_MAMS.service.MentorAssignmentService;
import com.projexflow.mams.ProjexFlow_MAMS.service.GmsClient;
import com.projexflow.mams.ProjexFlow_MAMS.service.UmsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
@Service
@RequiredArgsConstructor
public class MentorAssignmentImpl implements MentorAssignmentService {
    @Autowired
    private final MentorGroupRepository repo;
    @Autowired
    private final GmsClient gmsClient;
    @Autowired
    private final UmsClient umsClient;

    @Transactional
    public AssignResponse wipeAndAssign(AssignRequest req) {

        // 1) Pull fresh data
        List<Long> groupIds = gmsClient.getFinalGroupIds(req.getBatchId());     // final groups
        List<Long> mentorIds = umsClient.getAvailableMentorIds(req.getCourse()); // ONLY available mentors

        if (groupIds.isEmpty()) {
            // Nothing to assign, but still wipe to be consistent
            repo.deleteAllByBatchId(req.getBatchId());
            return new AssignResponse(req.getBatchId(), req.getCourse(), 0, mentorIds.size(), 0);
        }

        if (mentorIds.isEmpty()) {
            // cannot satisfy "no group unassigned"
            throw new RuntimeException("No available mentors for course=" + req.getCourse()
                    + ". Cannot assign " + groupIds.size() + " groups.");
        }

        // 2) WIPE existing assignments for that batch
        repo.deleteAllByBatchId(req.getBatchId());

        // 3) Balanced assignment using min-heap (mentor with least load)
        PriorityQueue<long[]> pq = new PriorityQueue<>(
                Comparator.<long[]>comparingLong(a -> a[1]).thenComparingLong(a -> a[0])
        );

        for (Long mid : mentorIds) pq.add(new long[]{mid, 0}); // [mentorId, load]

        int assignedCount = 0;

        for (Long gid : groupIds) {
            long[] leastLoaded = pq.poll();
            assert leastLoaded != null;
            Long mentorId = leastLoaded[0];

            MentorGroup row = new MentorGroup();
            row.setBatchId(req.getBatchId());
            row.setMentorId(mentorId);
            row.setGroupId(gid);
            row.setAssignedAt(LocalDateTime.now());
            repo.save(row);

            assignedCount++;
            leastLoaded[1] = leastLoaded[1] + 1;
            pq.add(leastLoaded);
        }

        // 4) Guarantee check (optional but aligns with your NOTE)
        if (assignedCount != groupIds.size()) {
            throw new RuntimeException("Assignment incomplete: assigned=" + assignedCount + ", groups=" + groupIds.size());
        }

        return new AssignResponse(req.getBatchId(), req.getCourse(), groupIds.size(), mentorIds.size(), assignedCount);
    }

    public List<Long> getAssignedGroupIds(Long mentorId, Long batchId) {
        return repo.findGroupIdsByMentorIdAndBatchId(mentorId, batchId);
    }

    @Override
    public List<AssignedGroupDetailsResponse> getAssignedGroupsDetailed(Long mentorId, Long batchId) {
        List<MentorGroup> rows = repo.findByMentorIdAndBatchId(mentorId, batchId);
        // Sort for stable UI output
        rows.sort(Comparator.comparing(MentorGroup::getGroupId));

        return rows.stream().map(r -> new AssignedGroupDetailsResponse(
                r.getMentorId(),
                r.getBatchId(),
                r.getGroupId(),
                r.getAssignedAt(),
                gmsClient.getGroupDetails(r.getGroupId())
        )).toList();
    }


    @Override
    public MentorForGroupResponse getMentorForGroup(Long batchId, Long groupId) {
        return repo.findByBatchIdAndGroupId(batchId, groupId)
                .map(mg -> MentorForGroupResponse.builder()
                        .mentorId(mg.getMentorId())
                        .assignedAt(mg.getAssignedAt())
                        .build())
                .orElse(null);
    }

    @Override
    public long countAssignedGroups(Long mentorId, Long batchId) {
        return repo.countByMentorIdAndBatchId(mentorId, batchId);
    }

}
