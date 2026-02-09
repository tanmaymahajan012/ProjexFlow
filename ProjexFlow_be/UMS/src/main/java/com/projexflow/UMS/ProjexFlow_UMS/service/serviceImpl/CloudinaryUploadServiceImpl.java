package com.projexflow.UMS.ProjexFlow_UMS.service.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.projexflow.UMS.ProjexFlow_UMS.service.CloudinaryUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryUploadServiceImpl implements CloudinaryUploadService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.profile-folder:projexflow/profiles}")
    private String profileFolder;

    @Override
    public String uploadProfilePhoto(MultipartFile file, Long studentId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Profile photo file is required");
        }

        try {
            Map<?, ?> res = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", profileFolder,
                            "public_id", "student_" + studentId,
                            "overwrite", true,
                            "resource_type", "image"
                    )
            );
            // secure_url is HTTPS
            Object secureUrl = res.get("secure_url");
            return secureUrl != null ? secureUrl.toString() : res.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile photo", e);
        }
    }
}
