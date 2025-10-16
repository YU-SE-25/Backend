// 토큰 재발급 성공 시 클라이언트에 반환하는 DTO

package com.unide.backend.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenRefreshResponseDto {
    private String accessToken;
    private Long expiresIn;
}
