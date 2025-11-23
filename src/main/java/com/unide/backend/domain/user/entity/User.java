// 사용자 엔터티 클래스

package com.unide.backend.domain.user.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.terms.entity.UserTermsConsent;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, unique = true, length = 255)
    private String phone;

    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 문자열로 저장
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private LocalDateTime emailVerifiedAt;

    private LocalDateTime lastLoginAt;

    private LocalDateTime passwordUpdatedAt;

    @Column(nullable = false)
    private int loginFailureCount;

    private LocalDateTime lockoutUntil;

    @Column(nullable = false)
    private boolean isSocialAccount;

    private LocalDateTime instructorVerifiedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTermsConsent> userTermsConsents = new ArrayList<>();

    @Builder // 빌더 패턴으로 객체를 생성할 수 있게 함.
    public User(String email, String passwordHash, String name, String nickname, String phone, UserRole role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.role = role;
        this.status = UserStatus.PENDING;
        this.loginFailureCount = 0;
        this.isSocialAccount = false;
    }

    // 연관관계 편의 메서드
    public void addUserTermsConsent(UserTermsConsent consent) {
        this.userTermsConsents.add(consent);
        consent.setUser(this);
    }

    // 계정 활성화 메서드
    public void activateAccount() {
        this.status = UserStatus.ACTIVE;
        this.emailVerifiedAt = LocalDateTime.now();
    }

    public void onLoginSuccess() {
        this.lastLoginAt = LocalDateTime.now();
        this.loginFailureCount = 0;
        this.lockoutUntil = null;
    }

    public void onLoginFailure(int maxFailures, Duration lockoutDuration) {
        this.loginFailureCount++;

        if (this.loginFailureCount >= maxFailures) {
            long lockoutDurationMinutes = lockoutDuration.toMinutes();
            this.lockoutUntil = LocalDateTime.now().plusMinutes(lockoutDurationMinutes);
        }
    }

    public boolean isLocked() {
        return this.lockoutUntil != null && this.lockoutUntil.isAfter(LocalDateTime.now());
    }

    public void changeRole(UserRole newRole) {
        if (newRole == UserRole.INSTRUCTOR) {
            this.instructorVerifiedAt = LocalDateTime.now();
        } else {
            this.instructorVerifiedAt = null;
        }
        this.role = newRole;
    }

    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.passwordUpdatedAt = LocalDateTime.now();
        this.loginFailureCount = 0;
        this.lockoutUntil = null;
    }

    public void markAsSocialAccount() {
        this.isSocialAccount = true;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
