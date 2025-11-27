// 회원 탈퇴 요청 DTO

package com.unide.backend.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WithdrawRequestDto {
    // 일반 사용자는 필수, 소셜 사용자는 null 가능
    private String password;
}
