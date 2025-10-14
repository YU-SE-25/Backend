// 사용자 엔티티 클래스

package com.unide.backend.domain.user.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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

    @Column(nullable = false, unique = true, length = 20)
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

    @Builder // 빌더 패턴으로 객체를 생성할 수 있게 합니다.
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
}
