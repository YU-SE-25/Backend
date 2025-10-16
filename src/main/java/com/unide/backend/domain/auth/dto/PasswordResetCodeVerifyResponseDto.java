// 비밀번호 재설정 코드 검증 성공 시 클라이언트에 반환하는 DTO

package com.unide.backend.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetCodeVerifyResponseDto {
    private String resetToken;
    private String message;
}
