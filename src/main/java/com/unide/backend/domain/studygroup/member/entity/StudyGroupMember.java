package com.unide.backend.domain.studygroup.member.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.unide.backend.domain.studygroup.group.entity.StudyGroup;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "study_group_member")
public class StudyGroupMember {

    @EmbeddedId
    private StudyGroupMemberId id;

    @MapsId("groupId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    protected StudyGroupMember() {
        // JPA 기본 생성자
    }

    private StudyGroupMember(StudyGroup group, User member) {
        this.group = group;
        this.member = member;
        this.id = new StudyGroupMemberId(group.getGroupId(), member.getId());
    }

    public static StudyGroupMember create(StudyGroup group, User member) {
        return new StudyGroupMember(group, member);
    }

    public StudyGroupMemberId getId() {
        return id;
    }

    public StudyGroup getGroup() {
        return group;
    }

    public User getMember() {
        return member;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
