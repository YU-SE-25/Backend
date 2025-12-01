package com.unide.backend.domain.studygroup.group.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.studygroup.group.entity.StudyGroup;

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
public class StudyGroupDetailResponse {

    @JsonProperty("groupId")
    private Long groupId;

    @JsonProperty("groupName")
    private String groupName;

    @JsonProperty("groupDescription")
    private String groupDescription;

    @JsonProperty("maxMembers")
    private int maxMembers;

    @JsonProperty("currentMembers")
    private int currentMembers;

    @JsonProperty("leader")
    private StudyGroupMemberSummary leader;

    @JsonProperty("members")
    private List<StudyGroupMemberSummary> members;

    @JsonProperty("myRole")
    private String myRole; // "LEADER" / "MEMBER" / "NONE"

    public static StudyGroupDetailResponse fromEntity(
            StudyGroup group,
            StudyGroupMemberSummary leader,
            List<StudyGroupMemberSummary> members,
            String myRole
    ) {
        return StudyGroupDetailResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupDescription(group.getGroupGoal())
                .maxMembers(group.getGroupTo())
                .currentMembers(group.getMemberCount())
                .leader(leader)
                .members(members)
                .myRole(myRole)
                .build();
    }
}
