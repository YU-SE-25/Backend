// 역할 변경 성공 시 클라이언트에 반환하는 DTO

package com.unide.backend.domain.admin.dto;

import com.unide.backend.domain.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleChangeResponseDto {
    private Long userId;
    private UserRole oldRole;
    private UserRole newRole;
    private String message;
}
