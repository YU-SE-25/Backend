// 리프레시 토큰 정보를 관리하는 엔터티

package com.unide.backend.domain.auth.entity;

import com.unide.backend.domain.user.entity.User;
import com.unide.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity { 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 토큰 값 자체를 저장, 중복 발급을 방지
    @Column(name = "token_value", nullable = false, unique = true, length = 500)
    private String tokenValue; 

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Builder
    public RefreshToken(User user, String tokenValue, LocalDateTime expiresAt) {
        this.user = user;
        this.tokenValue = tokenValue;
        this.expiresAt = expiresAt;
    }

    // 토큰 재발급 시 갱신 로직
    public void updateToken(String newTokenValue, LocalDateTime newExpiresAt) {
        this.tokenValue = newTokenValue;
        this.expiresAt = newExpiresAt;
    }
}
