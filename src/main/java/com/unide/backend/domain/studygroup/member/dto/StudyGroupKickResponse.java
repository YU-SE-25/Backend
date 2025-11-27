package com.unide.backend.domain.studygroup.member.dto;

public class StudyGroupKickResponse {

    private Long groupId;
    private Long kickedUserId;
    private String kickedUserName;
    private String status;

    public StudyGroupKickResponse(Long groupId, Long kickedUserId, String kickedUserName, String status) {
        this.groupId = groupId;
        this.kickedUserId = kickedUserId;
        this.kickedUserName = kickedUserName;
        this.status = status;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getKickedUserId() {
        return kickedUserId;
    }

    public String getKickedUserName() {
        return kickedUserName;
    }

    public String getStatus() {
        return status;
    }
}
