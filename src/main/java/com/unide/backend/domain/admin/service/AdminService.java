// 관리자 기능 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.admin.service;

import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.entity.UserRole;
import com.unide.backend.domain.user.entity.UserStatus;
import com.unide.backend.domain.user.repository.UserRepository;
import com.unide.backend.domain.admin.dto.RoleChangeRequestDto;
import com.unide.backend.domain.admin.dto.RoleChangeResponseDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationDetailDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationSummaryDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationListResponseDto;
import com.unide.backend.domain.instructor.entity.ApplicationStatus;
import com.unide.backend.domain.instructor.entity.InstructorApplication;
import com.unide.backend.domain.instructor.repository.InstructorApplicationRepository;
import com.unide.backend.domain.admin.dto.InstructorApplicationUpdateRequestDto;
import com.unide.backend.global.security.auth.PrincipalDetails;
import com.unide.backend.domain.admin.dto.UserListResponseDto;
import com.unide.backend.domain.admin.dto.UserSummaryDto;
import com.unide.backend.domain.admin.dto.BlacklistListResponseDto;
import com.unide.backend.domain.admin.dto.BlacklistSummaryDto;
import com.unide.backend.domain.admin.entity.Blacklist;
import com.unide.backend.domain.admin.repository.BlacklistRepository;
import com.unide.backend.domain.admin.dto.BlacklistCreateRequestDto;
import com.unide.backend.domain.admin.dto.BlacklistCreateResponseDto;
import com.unide.backend.domain.admin.dto.BlacklistDeleteResponseDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final UserRepository userRepository;
    private final InstructorApplicationRepository instructorApplicationRepository;
    private final BlacklistRepository blacklistRepository;

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

    @Transactional
    public InstructorApplicationDetailDto updateApplicationStatus(Long applicationId, InstructorApplicationUpdateRequestDto requestDto) {
        // 현재 인증된 관리자 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User adminUser = principalDetails.getUser();

        // ID로 지원서를 조회
        InstructorApplication application = instructorApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 강사 지원서를 찾을 수 없습니다: " + applicationId));

        User applicantUser = application.getUser();

        // 엔터티의 상태 변경 메서드 호출
        if (requestDto.getStatus() == ApplicationStatus.APPROVED) {
            application.approve(adminUser);
            applicantUser.changeRole(UserRole.INSTRUCTOR);
        } else if (requestDto.getStatus() == ApplicationStatus.REJECTED) {
            if (requestDto.getRejectionReason() == null || requestDto.getRejectionReason().isBlank()) {
                throw new IllegalArgumentException("거절 시에는 사유를 반드시 입력해야 합니다.");
            }
            application.reject(adminUser, requestDto.getRejectionReason());
        } else {
            // PENDING 상태로는 변경 불가
             throw new IllegalArgumentException("지원 상태는 APPROVED 또는 REJECTED로만 변경 가능합니다.");
        }
        
        // 변경된 상세 정보를 다시 조회하여 반환
        return getApplicationDetail(applicationId);
    }

    public UserListResponseDto getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserSummaryDto> userDtos = userPage.getContent().stream()
                .map(user -> UserSummaryDto.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .nickname(user.getNickname())
                        .phone(user.getPhone())
                        .role(user.getRole())
                        .status(user.getStatus())
                        .createdAt(user.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return UserListResponseDto.builder()
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .currentPage(userPage.getNumber())
                .users(userDtos)
                .build();
    }

    public BlacklistListResponseDto getBlacklist(Pageable pageable) {
        Page<Blacklist> blacklistPage = blacklistRepository.findAll(pageable);

        List<BlacklistSummaryDto> blacklistDtos = blacklistPage.getContent().stream()
                .map(b -> BlacklistSummaryDto.builder()
                        .blacklistId(b.getId())
                        .name(b.getName())
                        .email(b.getEmail())
                        .phone(b.getPhone())
                        .reason(b.getReason())
                        .bannedAt(b.getBannedAt())
                        .build())
                .collect(Collectors.toList());

        return BlacklistListResponseDto.builder()
                .totalElements(blacklistPage.getTotalElements())
                .totalPages(blacklistPage.getTotalPages())
                .currentPage(blacklistPage.getNumber())
                .blacklist(blacklistDtos)
                .build();
    }

    @Transactional
    public BlacklistCreateResponseDto addToBlacklist(User adminUser, BlacklistCreateRequestDto requestDto) {
        if (!StringUtils.hasText(requestDto.getEmail()) && !StringUtils.hasText(requestDto.getPhone())) {
            throw new IllegalArgumentException("이메일 또는 전화번호 중 하나는 반드시 입력해야 합니다.");
        }

        if (blacklistRepository.existsByEmailOrPhone(requestDto.getEmail(), requestDto.getPhone())) {
            throw new IllegalArgumentException("이미 블랙리스트에 등록된 사용자입니다.");
        }

        if (StringUtils.hasText(requestDto.getEmail())) {
            userRepository.findByEmail(requestDto.getEmail())
                    .ifPresent(user -> user.changeStatus(UserStatus.SUSPENDED));
        }

        Blacklist blacklist = Blacklist.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .phone(requestDto.getPhone())
                .reason(requestDto.getReason())
                .bannedBy(adminUser)
                .build();
        
        Blacklist savedBlacklist = blacklistRepository.save(blacklist);

        return BlacklistCreateResponseDto.builder()
                .blacklistId(savedBlacklist.getId())
                .message("블랙리스트에 등록되었습니다." + (requestDto.getEmail() != null ? " (관련 계정 정지됨)" : ""))
                .bannedAt(savedBlacklist.getBannedAt())
                .build();
    }

    @Transactional
    public BlacklistDeleteResponseDto removeFromBlacklist(Long blacklistId) {
        Blacklist blacklist = blacklistRepository.findById(blacklistId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 블랙리스트 ID입니다: " + blacklistId));

        if (blacklist.getEmail() != null) {
            userRepository.findByEmail(blacklist.getEmail())
                    .ifPresent(user -> user.changeStatus(UserStatus.ACTIVE));
        }

        if (blacklist.getPhone() != null) {
            userRepository.findByPhone(blacklist.getPhone())
                    .ifPresent(user -> user.changeStatus(UserStatus.ACTIVE));
        }

        blacklistRepository.delete(blacklist);
        
        return BlacklistDeleteResponseDto.builder()
                .blacklistId(blacklistId)
                .message("블랙리스트에서 해제되었으며, 관련 계정이 복구되었습니다.")
                .unbannedAt(LocalDateTime.now())
                .build();
    }
}
