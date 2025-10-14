// 사용자의 약관 동의 내역을 관리하는 엔터티

package com.unide.backend.domain.terms.entity;

import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "user_terms_consent")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTermsConsent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "terms_code", nullable = false, length = 50)
    private Terms termsCode;

    @Column(nullable = false, length = 20)
    private String version;

    @Column(nullable = false)
    private boolean agreed;

    private LocalDateTime agreedAt;

    @Builder
    public UserTermsConsent(User user, Terms termsCode, String version, boolean agreed) {
        this.user = user;
        this.termsCode = termsCode;
        this.version = version;
        this.agreed = agreed;
        if (agreed) {
            this.agreedAt = LocalDateTime.now();
        }
    }

    // 연관관계 편의 메서드 (User 클래스에서 호출됨)
    public void setUser(User user) {
        this.user = user;
    }
}
