// 강사 지원 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.instructor.service;

import com.unide.backend.domain.instructor.dto.InstructorApplicationRequestDto;
import com.unide.backend.domain.instructor.dto.InstructorApplicationResponseDto;
import com.unide.backend.domain.instructor.entity.InstructorApplication;
import com.unide.backend.domain.instructor.repository.InstructorApplicationRepository;
import com.unide.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstructorService {
    private final InstructorApplicationRepository instructorApplicationRepository;

    @Transactional
    public InstructorApplicationResponseDto submitApplication(User user, InstructorApplicationRequestDto requestDto) {
        instructorApplicationRepository.findByUserId(user.getId()).ifPresent(app -> {
            throw new IllegalStateException("이미 강사 지원서가 제출되었습니다.");
        });

        // portfolioLinks 리스트를 DB에 저장하기 위해 문자열로 변환
        String linksAsString = requestDto.getPortfolioLinks() != null ?
                String.join("\n", requestDto.getPortfolioLinks()) : null;

        InstructorApplication application = InstructorApplication.builder()
                .user(user)
                .portfolioFileUrl(requestDto.getUploadedFileUrl()) // 파일 업로드 API에서 받은 키/URL
                .portfolioLinks(linksAsString)
                .build(); // status는 엔티티 Builder에서 PENDING으로 자동 설정됨

        InstructorApplication savedApplication = instructorApplicationRepository.save(application);

        return InstructorApplicationResponseDto.builder()
                .applicationId(savedApplication.getId())
                .message("포트폴리오 제출이 완료되었습니다. 관리자 승인을 기다려주세요.")
                .build();
    }
}
