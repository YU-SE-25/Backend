package com.unide.backend.domain.studygroup.group.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.unide.backend.domain.studygroup.group.entity.StudyGroup;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyGroupListItemResponse {

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

    @JsonProperty("leaderName")
    private String leaderName; // 지금은 null, 나중에 User와 조인해서 채워도 됨

    @JsonProperty("myRole")
    private String myRole;     // "LEADER" / "MEMBER" / "NONE"

    public static StudyGroupListItemResponse fromEntity(StudyGroup group,
                                                        String leaderName,
                                                        String myRole) {
        return StudyGroupListItemResponse.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupDescription(group.getGroupGoal())
                .maxMembers(group.getGroupTo())
                .currentMembers(group.getMemberCount())
                .leaderName(leaderName)
                .myRole(myRole)
                .build();
    }
}
