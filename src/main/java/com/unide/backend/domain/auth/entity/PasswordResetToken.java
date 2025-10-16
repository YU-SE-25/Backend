// 비밀번호 재설정 토큰 정보를 관리하는 엔티티

package com.unide.backend.domain.auth.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "password_reset_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reset_token", nullable = false, unique = true)
    private String resetToken; // 비밀번호 변경 권한 부여용 임시 토큰

    @Column(name = "verification_code", nullable = false, length = 6)
    private String verificationCode; // 사용자에게 발송되는 6자리 인증 코드

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    @Builder
    public PasswordResetToken(User user, String resetToken, String verificationCode, LocalDateTime expiresAt) {
        this.user = user;
        this.resetToken = resetToken;
        this.verificationCode = verificationCode;
        this.expiresAt = expiresAt;
    }

    public void useToken() {
        this.usedAt = LocalDateTime.now();
    }
}
