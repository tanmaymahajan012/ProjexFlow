package com.projexflow.UMS.ProjexFlow_UMS.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Uploads media to Cloudinary and returns the public URL.
 */
public interface CloudinaryUploadService {
    String uploadProfilePhoto(MultipartFile file, Long studentId);
}
