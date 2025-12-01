package com.unide.backend.domain.mypage.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
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
        if (this.isPublic == null) this.isPublic = true;
        if (this.isDarkMode == null) this.isDarkMode = false;
        if (this.isStudyAlarm == null) this.isStudyAlarm = false;
    }

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
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

    // 마이페이지 공개 여부
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isPublic;

    // 스터디 알림 설정 여부
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isStudyAlarm;  

    // 다크 모드 설정 여부
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDarkMode;     


    @Builder
    public MyPage(User user,
                  String nickname,
                  String avatarUrl,
                  String bio,
                  String preferredLanguage,
                  Boolean isPublic,
                  Boolean isStudyAlarm,
                  Boolean isDarkMode) {

        this.user = user;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.preferredLanguage = preferredLanguage;

        this.isPublic = (isPublic != null) ? isPublic : true;
        this.isStudyAlarm = (isStudyAlarm != null) ? isStudyAlarm : false;
        this.isDarkMode = (isDarkMode != null) ? isDarkMode : false;
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

    public void updateIsStudyAlarm(Boolean isStudyAlarm) {
        this.isStudyAlarm = isStudyAlarm;
    }

    public void updateIsDarkMode(Boolean isDarkMode) {
        this.isDarkMode = isDarkMode;
    }
}
