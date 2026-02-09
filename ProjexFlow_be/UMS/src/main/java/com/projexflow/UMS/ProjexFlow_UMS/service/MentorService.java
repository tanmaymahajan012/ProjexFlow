package com.projexflow.UMS.ProjexFlow_UMS.service;

import com.projexflow.UMS.ProjexFlow_UMS.dto.MentorCreateRequest;
import com.projexflow.UMS.ProjexFlow_UMS.dto.MentorResponse;
import com.projexflow.UMS.ProjexFlow_UMS.dto.MentorUpdateRequest;


import java.util.List;

public interface MentorService {
    MentorResponse create(MentorCreateRequest req) ;
    MentorResponse update(Long id, MentorUpdateRequest req);
    MentorResponse getById(Long id) ;
    List<MentorResponse> getAll() ;
}
