package com.unide.backend.domain.studygroup.group.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.unide.backend.domain.studygroup.member.entity.StudyGroupLog;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.OneToMany;
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
@Table(name = "study_group")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", nullable = false, length = 20)
    private String groupName;

    // 그룹장 user.id
    @Column(name = "group_leader")
    private Long groupLeader;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "member_count", nullable = false)
    private int memberCount;

    @Column(name = "last_time", nullable = false)
    private LocalDateTime lastTime;

    @Column(name = "group_goal", length = 50)
    private String groupGoal;

    @Column(name = "group_photo", length = 255)
    private String groupPhoto;

    // 정원 (maxMembers 느낌)
    @Column(name = "group_TO", nullable = false)
    private int groupTo;

    // 🔥 추가: 스터디 그룹 활동 로그 (1:N)
    @OneToMany(
            mappedBy = "group",          // StudyGroupLog 의 필드명 그대로
            cascade = CascadeType.ALL,   // 삭제/저장 모두 전파
            orphanRemoval = true
    )
    private List<StudyGroupLog> logs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastTime = this.createdAt;
        if (this.memberCount == 0) {
            this.memberCount = 1;
        }
        if (this.groupTo == 0) {
            this.groupTo = 2;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastTime = LocalDateTime.now();
    }

    public Long getGroupId() {
        return groupId;
    }

    public int getGroupTo() {
        return groupTo;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}
