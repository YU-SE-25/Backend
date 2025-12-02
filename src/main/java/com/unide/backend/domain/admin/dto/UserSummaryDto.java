// 관리자용 사용자 목록의 각 항목 정보 DTO

package com.unide.backend.domain.admin.dto;

import com.unide.backend.domain.user.entity.UserRole;
import com.unide.backend.domain.user.entity.UserStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserSummaryDto {
    private Long userId;
    private String email;
    private String name;
    private String nickname;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
}
