package com.unide.backend.domain.studygroup.group.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyGroupCreateRequest {

    @JsonProperty("groupName")
    private String groupName;

    // 화면에서 “설명”으로 쓰고 싶으면 이걸 goal 로 쓸 수 있음
    @JsonProperty("groupDescription")
    private String groupDescription;

    @JsonProperty("maxMembers")
    private Integer maxMembers;  // null 이면 기본 2
}
