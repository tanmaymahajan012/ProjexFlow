package com.projexflow.UMS.ProjexFlow_UMS.service;

import com.projexflow.UMS.ProjexFlow_UMS.dto.*;

import java.util.List;

public interface StudentService {

    StudentResponse create(StudentCreateRequest req) ;
    StudentResponse update(Long id, StudentUpdateRequest req);
    StudentResponse getById(Long id) ;
    List<StudentResponse> getAll() ;
   StudentResponse getByPrn(String prn) ;
   List<StudentResponse> getByBatchIdAndCourse(Long batchId, String course) ;
   List<StudentResponse> getByBatchId(Long batchId);
    List<Long> getAvailableBatchIds();
    List<String> getAvailableCourses();


    /** Persist a newly uploaded profile photo URL for UI rendering. */
   StudentResponse updateProfilePhotoUrl(Long id, String profilePhotoUrl);

   /** Batch fetch students by IDs (used by GMS/TMS for UI-friendly responses). */
   List<StudentResponse> getByIds(List<Long> ids);

   /** Resolve a student by email (used when downstream services only have JWT subject/email). */
   StudentResponse getByEmail(String email);


}
