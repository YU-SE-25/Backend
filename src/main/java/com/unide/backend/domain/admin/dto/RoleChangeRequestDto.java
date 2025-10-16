// 관리자가 사용자 역할을 변경할 때 요청을 담는 DTO

package com.unide.backend.domain.admin.dto;

import com.unide.backend.domain.user.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoleChangeRequestDto {
    @NotNull(message = "새로운 역할은 필수 입력 값입니다.")
    private UserRole newRole;
}
