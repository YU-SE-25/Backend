// 로그인 성공 응답 시 토큰과 사용자 정보를 담는 DTO

package com.unide.backend.domain.auth.dto;

import com.unide.backend.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor 
public class LoginResponseDto {
    
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private UserInfo user;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private UserRole role;
    }
}
