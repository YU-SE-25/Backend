// 로그아웃 요청 시 클라이언트의 요청을 담는 DTO

package com.unide.backend.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutRequestDto {
    @NotBlank(message = "리프레시 토큰은 필수 입력 값입니다.")
    private String refreshToken;
}
