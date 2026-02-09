package com.projexflow.tms.ProjexFlow_TMS.service;


import java.util.Set;

public interface ExternalIntegrationService {

    Set<Long> getGroupIdsForMentor(Long mentorId, Long batchId);

    Set<Long> getStudentIdsForGroup(Long groupId);

    String getRepoUrlForGroup(Long batchId, Long groupId);

    Long getGroupIdForStudent(Long batchId, Long studentId);
}

