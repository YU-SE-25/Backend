package com.unide.backend.domain.mypage.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocalImageService implements ImageService {

    @Value("${app.upload.avatar-dir}")
    private String uploadRootDir;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final String PROFILE_FOLDER = "profile-images";

    @Override
    public String uploadProfileImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지가 없습니다.");
        }

        try {
            Path uploadDir = Paths.get(uploadRootDir, PROFILE_FOLDER)
                    .toAbsolutePath().normalize();

            Files.createDirectories(uploadDir);

            String extension = getExtension(file.getOriginalFilename());
            String savedName = UUID.randomUUID() + extension;

            Path targetPath = uploadDir.resolve(savedName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // ⭐ 절대 URL 반환!
            return baseUrl + "/uploads/" + PROFILE_FOLDER + "/" + savedName;

        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장 실패: " + e.getMessage(), e);
        }
    }
    @Override
    public void deleteImage(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) return;

        try {
            // 절대 URL → 상대 경로로 변환
            // http://localhost:8080/uploads/profile-images/xxx.png
            String relative = imageUrl.replace(baseUrl + "/", "");  
            // uploads/profile-images/xxx.png

            Path filePath = Paths.get(relative).toAbsolutePath().normalize();

            Files.deleteIfExists(filePath);

        } catch (Exception e) {
            System.out.println("이미지 삭제 실패 (무시 가능): " + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf(".");
        return (idx > -1) ? filename.substring(idx) : "";
    }
}


