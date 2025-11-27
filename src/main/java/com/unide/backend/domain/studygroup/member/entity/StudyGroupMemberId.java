package com.unide.backend.domain.studygroup.member.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class StudyGroupMemberId implements Serializable {

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "member_id")
    private Long memberId;

    protected StudyGroupMemberId() {
        // JPA 기본 생성자
    }

    public StudyGroupMemberId(Long groupId, Long memberId) {
        this.groupId = groupId;
        this.memberId = memberId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getMemberId() {
        return memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudyGroupMemberId)) return false;
        StudyGroupMemberId that = (StudyGroupMemberId) o;
        return Objects.equals(groupId, that.groupId)
                && Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, memberId);
    }
}
