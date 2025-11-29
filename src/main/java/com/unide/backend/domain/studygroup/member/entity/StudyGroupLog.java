package com.unide.backend.domain.studygroup.member.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.unide.backend.domain.studygroup.group.entity.StudyGroup;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "study_group_log")
public class StudyGroupLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private StudyGroupActivityType activityType;

    @ManyToOne(fetch = FetchType.LAZY)
<<<<<<< HEAD
    @JoinColumn(name = "actor_user_id")
=======
    @JoinColumn(name = "actor_user_id") // nullable 여부는 스키마에 맞추면 됨
>>>>>>> bc4f78f ([추가]스터디그룹 로그)
    private User actorUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "ref_entity_type")
    private StudyGroupRefEntityType refEntityType;

    @Column(name = "ref_entity_id")
    private Long refEntityId;

    @Column(name = "message", length = 200)
    private String message;

    @Column(name = "payload", columnDefinition = "json")
<<<<<<< HEAD
    private String payload; // 필요하면 나중에 JsonNode 로 변경 가능
=======
    private String payload;
>>>>>>> bc4f78f ([추가]스터디그룹 로그)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected StudyGroupLog() {
    }

    private StudyGroupLog(
            StudyGroup group,
            StudyGroupActivityType activityType,
            User actorUser,
            User targetUser,
            StudyGroupRefEntityType refEntityType,
            Long refEntityId,
            String message
    ) {
        this.group = group;
        this.activityType = activityType;
        this.actorUser = actorUser;
        this.targetUser = targetUser;
        this.refEntityType = refEntityType;
        this.refEntityId = refEntityId;
        this.message = message;
    }

    public static StudyGroupLog of(
            StudyGroup group,
            StudyGroupActivityType activityType,
            User actorUser,
            User targetUser,
            StudyGroupRefEntityType refEntityType,
            Long refEntityId,
            String message
    ) {
        return new StudyGroupLog(group, activityType, actorUser, targetUser, refEntityType, refEntityId, message);
    }
}
