package com.unide.backend.domain.studygroup.group.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "GroupStudyMember")
@Table(name = "study_group_member")
public class GroupStudyMember {

    @EmbeddedId
    private GroupStudyMemberId id;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }

    public static GroupStudyMember of(Long groupId, Long memberId) {
        GroupStudyMemberId id = new GroupStudyMemberId(groupId, memberId);

        GroupStudyMember member = new GroupStudyMember();
        member.setId(id);
        // joinedAt 은 @PrePersist에서 자동 세팅
        return member;
    }
}
