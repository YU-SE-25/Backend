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

    @Value("${app.upload.avatar-dir}")  // 예: uploads
    private String uploadRootDir;

    private static final String PROFILE_FOLDER = "profile-images";

    @Override
    public String uploadProfileImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지가 없습니다.");
        }

        try {
            // 1. 폴더 생성
            Path uploadDir = Paths.get(uploadRootDir, PROFILE_FOLDER)
                    .toAbsolutePath().normalize();

            Files.createDirectories(uploadDir);

            // 2. 확장자 추출
            String originalName = file.getOriginalFilename();
            String extension = getExtension(originalName);

            // 3. 고유 파일명 생성
            String savedName = UUID.randomUUID() + extension;

            // 4. 저장 위치
            Path targetPath = uploadDir.resolve(savedName);

            // 5. 실제 파일 저장
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 6. DB에는 /uploads/... 형태로 저장
            return "/uploads/" + PROFILE_FOLDER + "/" + savedName;

        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장 실패: " + e.getMessage(), e);
        }
    }


    @Override
    public void deleteImage(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) return;

        try {
            // /uploads/profile-images/xxxx.png → 실제 경로로 변환
            String relativePath = imageUrl.replace("/uploads/", "");

            Path filePath = Paths.get(uploadRootDir)
                    .toAbsolutePath().normalize()
                    .resolve(relativePath);

            Files.deleteIfExists(filePath);

        } catch (Exception e) {
            System.out.println("이미지 삭제 실패 (무시 가능): " + e.getMessage());
        }
    }


    /** 확장자 추출 유틸리티 */
    private String getExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf(".");
        return (idx > -1) ? filename.substring(idx) : "";
    }
}
