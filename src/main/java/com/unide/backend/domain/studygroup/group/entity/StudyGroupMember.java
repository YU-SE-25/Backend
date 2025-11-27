package com.unide.backend.domain.studygroup.group.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "study_group_member")
public class StudyGroupMember {

    @EmbeddedId
    private StudyGroupMemberId id;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }

    public static StudyGroupMember of(Long groupId, Long memberId) {
        return StudyGroupMember.builder()
                .id(StudyGroupMemberId.builder()
                        .groupId(groupId)
                        .memberId(memberId)
                        .build())
                .build();
    }
}
