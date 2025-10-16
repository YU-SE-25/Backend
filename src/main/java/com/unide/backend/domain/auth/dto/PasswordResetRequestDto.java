// 비밀번호 최종 재설정 시 클라이언트의 요청을 담는 DTO

package com.unide.backend.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetRequestDto {
    @NotBlank(message = "재설정 토큰은 필수 입력 값입니다.")
    private String resetToken;

    @NotBlank(message = "새로운 비밀번호는 필수 입력 값입니다.")
    private String newPassword;
}
