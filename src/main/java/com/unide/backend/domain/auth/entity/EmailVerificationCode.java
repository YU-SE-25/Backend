// 이메일 인증 토큰 정보를 관리하는 엔터티

package com.unide.backend.domain.auth.entity;

import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "email_verification_code")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String verificationToken;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationPurpose purpose;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    @Builder
    public EmailVerificationCode(User user, String verificationToken, VerificationPurpose purpose, LocalDateTime expiresAt) {
        this.user = user;
        this.verificationToken = verificationToken;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
    }

    public enum VerificationPurpose {
        SIGNUP, PWD_RESET
    }
}
