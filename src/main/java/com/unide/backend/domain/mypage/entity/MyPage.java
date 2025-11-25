package com.unide.backend.domain.mypage.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "mypage")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPage extends BaseTimeEntity {

    @PrePersist
    public void prePersist() {
        if (this.isPublic == null) {
            this.isPublic = true;
        }
    }
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    // @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", insertable = false, updatable = false)
    // private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // User의 PK를 이 엔티티의 PK로 사용
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(length = 500)
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "JSON")
    private String preferredLanguage;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isPublic;

    // @Builder
    // public MyPage(Long userId, User user, String nickname, String avatarUrl, String bio, String preferredLanguage, Boolean isPublic) {
    //     this.userId = userId;
    //     this.user = user;
    //     this.nickname = nickname;
    //     this.avatarUrl = avatarUrl;
    //     this.bio = bio;
    //     this.preferredLanguage = preferredLanguage;
    //     this.isPublic = (isPublic != null) ? isPublic : true;
    // }

    @Builder
    public MyPage(User user, String nickname, String avatarUrl, String bio, String preferredLanguage, Boolean isPublic) {
        this.user = user;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.preferredLanguage = preferredLanguage;
        this.isPublic = (isPublic != null) ? isPublic : true;
        // userId는 @MapsId 덕분에 user.getId()와 자동으로 동기화됨
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updatePreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public void updateIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}
