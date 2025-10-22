// 파일 업로드 및 관련 로직을 처리하는 서비스 클래스

package com.unide.backend.common.file.service;

import com.unide.backend.common.file.dto.FileUploadResponseDto;
import com.unide.backend.domain.instructor.entity.UserPortfolioFile;
import com.unide.backend.domain.instructor.repository.UserPortfolioFileRepository;
import com.unide.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final UserPortfolioFileRepository userPortfolioFileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public FileUploadResponseDto uploadPortfolio(User user, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) { 
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String originalFileName = file.getOriginalFilename();
        // 저장될 파일 이름 생성
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path filePath = Paths.get(uploadDir, storedFileName);

        // 실제 파일 저장
        Files.copy(file.getInputStream(), filePath);

        // DB에 파일 정보 저장
        UserPortfolioFile portfolioFile = UserPortfolioFile.builder()
                .user(user)
                .originalName(originalFileName)
                .storedKey(storedFileName) // 로컬 저장 시에는 파일명 자체를 키로 사용
                .mimeType(file.getContentType())
                .sizeBytes(file.getSize())
                .storage(UserPortfolioFile.StorageType.LOCAL)
                .build();
        userPortfolioFileRepository.save(portfolioFile);

        // 응답 DTO 생성 (fileUrl은 저장된 파일명을 반환하여 다음 단계에서 사용)
        return FileUploadResponseDto.builder()
                .fileUrl(storedFileName) // 실제 접근 URL 대신 저장된 파일명을 반환
                .originalFileName(originalFileName)
                .fileSize(file.getSize())
                .build();
    }
}
