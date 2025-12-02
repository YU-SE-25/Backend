// 블랙리스트 정보를 관리하는 엔터티

package com.unide.backend.domain.admin.entity;

import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "blacklist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banned_by_id")
    private User bannedBy;

    @Column(nullable = false)
    private LocalDateTime bannedAt;

    @Builder
    public Blacklist(String name, String email, String phone, String reason, User bannedBy) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.reason = reason;
        this.bannedBy = bannedBy;
        this.bannedAt = LocalDateTime.now();
    }
}
