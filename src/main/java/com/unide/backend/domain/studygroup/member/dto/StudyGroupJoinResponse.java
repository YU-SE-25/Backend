package com.unide.backend.domain.studygroup.member.dto;

import java.time.LocalDateTime;

public class StudyGroupJoinResponse {

    private Long groupId;
    private Long userId;
    private String role;
    private String status;
    private StudyGroupCapacityDto capacity;
    private LocalDateTime joinedAt;

    public StudyGroupJoinResponse(
            Long groupId,
            Long userId,
            String role,
            String status,
            StudyGroupCapacityDto capacity,
            LocalDateTime joinedAt
    ) {
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
        this.status = status;
        this.capacity = capacity;
        this.joinedAt = joinedAt;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public StudyGroupCapacityDto getCapacity() {
        return capacity;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
