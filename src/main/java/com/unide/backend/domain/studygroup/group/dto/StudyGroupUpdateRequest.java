package com.unide.backend.domain.studygroup.group.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyGroupUpdateRequest {

    @JsonProperty("groupName")
    private String groupName;              // 선택

    @JsonProperty("groupDescription")
    private String groupDescription;       // 선택

    @JsonProperty("maxMembers")
    private Integer maxMembers;           // 선택

    // 새로 추가할 멤버 userId 리스트 (선택)
    @JsonProperty("groupMemberIds")
    private List<Long> groupMemberIds;
}
