package com.unide.backend.domain.studygroup.group.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class StudyGroupMemberSummary {

    @JsonProperty("groupMemberId")
    private Long groupMemberId; // 복합키 없으니까 일단 memberId 그대로 사용

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("userName")
    private String userName; // User 엔티티 붙이면 채우기

    @JsonProperty("role")
    private String role; // "LEADER" / "MEMBER"
}
