package com.projexflow.tms.ProjexFlow_TMS.service.serviceImpl;

import com.projexflow.tms.ProjexFlow_TMS.dto.external.ProjectResponse;
import com.projexflow.tms.ProjexFlow_TMS.service.ExternalIntegrationService;
import com.projexflow.tms.ProjexFlow_TMS.service.GmsClient;
import com.projexflow.tms.ProjexFlow_TMS.service.MamsClient;
import com.projexflow.tms.ProjexFlow_TMS.service.PmsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExternalIntegrationServiceImpl implements ExternalIntegrationService {

    private final MamsClient mamsClient;
    private final GmsClient gmsClient;
    private final PmsClient pmsClient;

    @Override
    public Set<Long> getGroupIdsForMentor(Long mentorId, Long batchId) {
        List<Long> ids = mamsClient.getAssignedGroupIds(mentorId, batchId);
        return ids == null ? Set.of() : new HashSet<>(ids);
    }

    @Override
    public Set<Long> getStudentIdsForGroup(Long groupId) {
        List<Long> ids = gmsClient.getActiveStudentIds(groupId);
        return ids == null ? Set.of() : new HashSet<>(ids);
    }

    @Override
    public String getRepoUrlForGroup(Long batchId, Long groupId) {
        ProjectResponse p = pmsClient.getByBatchAndGroup(batchId, groupId);
        return p == null ? null : p.repoUrl();
    }


    @Override
    public Long getGroupIdForStudent(Long batchId, Long studentId) {
        return gmsClient.getMyGroupId(batchId, studentId);
    }
}
