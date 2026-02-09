package com.projexflow.UMS.ProjexFlow_UMS.service;


import com.projexflow.UMS.ProjexFlow_UMS.dto.*;
import jakarta.transaction.Transactional;

import java.util.List;


public interface AdminService {
    AdminResponse create(AdminCreateRequest req) ;
    AdminResponse getById(Long id) ;
    List<AdminResponse> getAll() ;
    MentorResponse setMentorActive(Long mentorId, boolean active);
    StudentResponse setStudentActive(Long studentId, boolean active);
    AdminResponse update(Long id, AdminUpdateRequest req);
    @Transactional
    List<StudentResponse> createStudentsBulk(List<StudentCreateRequest> requests);
    @Transactional
    List<MentorResponse> createMentorsBulk(List<MentorCreateRequest> requests);


}

