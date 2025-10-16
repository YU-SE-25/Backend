// 환영 이메일 발송 시 클라이언트의 요청을 담는 DTO

package com.unide.backend.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WelcomeEmailRequestDto {
    @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
    private Long userId;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}
