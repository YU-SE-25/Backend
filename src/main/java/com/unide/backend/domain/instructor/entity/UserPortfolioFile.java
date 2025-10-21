// 사용자가 업로드한 포트폴리오 파일 정보를 관리하는 엔티티

package com.unide.backend.domain.instructor.entity;

import com.unide.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "user_portfolio_file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPortfolioFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false, unique = true)
    private String storedKey; // 로컬 저장 시 파일명 또는 S3 키

    private String mimeType;
    private Long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StorageType storage; // 저장소 구분

    private LocalDateTime uploadedAt;

    @Builder
    public UserPortfolioFile(User user, String originalName, String storedKey, String mimeType, Long sizeBytes, StorageType storage) {
        this.user = user;
        this.originalName = originalName;
        this.storedKey = storedKey;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.storage = storage;
        this.uploadedAt = LocalDateTime.now();
    }

    public enum StorageType {
        LOCAL, S3
    }
}
