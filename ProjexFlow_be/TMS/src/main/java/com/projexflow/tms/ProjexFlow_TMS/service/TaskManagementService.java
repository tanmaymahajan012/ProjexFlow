package com.projexflow.tms.ProjexFlow_TMS.service;

import com.projexflow.tms.ProjexFlow_TMS.dto.AssignTaskRequest;
import com.projexflow.tms.ProjexFlow_TMS.dto.AssignTaskResultResponse;
import com.projexflow.tms.ProjexFlow_TMS.dto.ReviewSubmissionRequest;
import com.projexflow.tms.ProjexFlow_TMS.dto.SubmitTaskRequest;
import com.projexflow.tms.ProjexFlow_TMS.entity.*;
import com.projexflow.tms.ProjexFlow_TMS.exception.BadRequestException;
import com.projexflow.tms.ProjexFlow_TMS.exception.ForbiddenException;
import com.projexflow.tms.ProjexFlow_TMS.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface TaskManagementService {


    @Transactional
    AssignTaskResultResponse assignToGroups(Long mentorId, Long taskId, AssignTaskRequest req) ;
    @Transactional
    AssignTaskResultResponse assignToAllGroups(Long mentorId, Long taskId, Long batchId, LocalDateTime dueAt);


    @Transactional
    Long submitTask(Long studentId, Long assignmentId, SubmitTaskRequest req) ;


    @Transactional
    Long reviewAssignment(Long mentorId, Long assignmentId, ReviewSubmissionRequest req) ;

    // ----------------- Read helpers used by controllers -----------------

    List<TaskSubmission> getSubmissionsForMentor(Long mentorId, Long assignmentId) ;

    List<TaskSubmission> getSubmissionsForStudent(Long studentId, Long assignmentId) ;
}
