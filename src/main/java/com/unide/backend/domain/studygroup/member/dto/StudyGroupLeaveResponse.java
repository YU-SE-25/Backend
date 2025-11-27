package com.unide.backend.domain.studygroup.member.dto;

public class StudyGroupLeaveResponse {

    private Long groupId;
    private Long userId;
    private String status;

    public StudyGroupLeaveResponse(Long groupId, Long userId, String status) {
        this.groupId = groupId;
        this.userId = userId;
        this.status = status;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }
}
