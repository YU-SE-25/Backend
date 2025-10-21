// 관리자 기능 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.admin.service;

import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.entity.UserRole;
import com.unide.backend.domain.user.repository.UserRepository;
import com.unide.backend.domain.admin.dto.RoleChangeRequestDto;
import com.unide.backend.domain.admin.dto.RoleChangeResponseDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationDetailDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationSummaryDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationListResponseDto;
import com.unide.backend.domain.instructor.entity.ApplicationStatus;
import com.unide.backend.domain.instructor.entity.InstructorApplication;
import com.unide.backend.domain.instructor.repository.InstructorApplicationRepository;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final UserRepository userRepository;
    private final InstructorApplicationRepository instructorApplicationRepository;

    @Transactional
    public RoleChangeResponseDto changeUserRole(Long userId, RoleChangeRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 사용자를 찾을 수 없습니다: " + userId));

        UserRole oldRole = user.getRole();
        UserRole newRole = requestDto.getNewRole();

        user.changeRole(newRole);

        return RoleChangeResponseDto.builder()
                .userId(user.getId())
                .oldRole(oldRole)
                .newRole(newRole)
                .message("사용자 역할이 성공적으로 변경되었습니다.")
                .build();
    }

    public InstructorApplicationListResponseDto getPendingApplications(Pageable pageable) {
        // PENDING 상태의 지원서 목록을 DB에서 조회 (사용자 정보 포함)
        Page<InstructorApplication> applicationPage = instructorApplicationRepository
                .findByStatusWithUser(ApplicationStatus.PENDING, pageable);

        // 조회된 엔티티 목록을 DTO 목록으로 변환
        List<InstructorApplicationSummaryDto> applicationDtos = applicationPage.getContent().stream()
                .map(app -> InstructorApplicationSummaryDto.builder()
                        .applicationId(app.getId())
                        .name(app.getUser().getName())
                        .email(app.getUser().getEmail())
                        .submittedAt(app.getCreatedAt())
                        .status(app.getStatus())
                        .build())
                .collect(Collectors.toList());

        // 최종 응답 DTO 생성 및 반환
        return InstructorApplicationListResponseDto.builder()
                .totalCount(applicationPage.getTotalElements())
                .currentPage(applicationPage.getNumber())
                .pageSize(applicationPage.getSize())
                .applications(applicationDtos)
                .build();
    }

    public InstructorApplicationDetailDto getApplicationDetail(Long applicationId) {
        // ID로 지원서 상세 정보 조회 (사용자, 처리자 정보 포함)
        InstructorApplication application = instructorApplicationRepository.findByIdWithDetails(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 강사 지원서를 찾을 수 없습니다: " + applicationId));

        User processor = application.getProcessor();

        // 엔티티를 DTO로 변환하여 반환
        return InstructorApplicationDetailDto.builder()
                .applicationId(application.getId())
                .userId(application.getUser().getId())
                .name(application.getUser().getName())
                .email(application.getUser().getEmail())
                .phone(application.getUser().getPhone())
                .submittedAt(application.getCreatedAt())
                .status(application.getStatus())
                .portfolioFileUrl(application.getPortfolioFileUrl())
                .portfolioLinks(application.getPortfolioLinks())
                .rejectionReason(application.getRejectionReason())
                .processedAt(application.getProcessedAt())
                .processorId(processor != null ? processor.getId() : null)
                .processorName(processor != null ? processor.getName() : null)
                .build();
    }
}
