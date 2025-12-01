package com.unide.backend.domain.studygroup.group.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.studygroup.group.entity.GroupStudyMember;
import com.unide.backend.domain.user.entity.User;

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
    private Long groupMemberId; // 여기엔 memberId 그대로 넣자

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("role")
    private String role; // "LEADER" / "MEMBER"

    /**
     * GroupStudyMember + User + leaderId 로 DTO 생성
     *
     * @param member   study_group_member 엔티티
     * @param user     memberId에 해당하는 User 엔티티
     * @param leaderId 이 그룹의 leader userId (없으면 null)
     */
    public static StudyGroupMemberSummary from(
            GroupStudyMember member,
            User user,
            Long leaderId
    ) {
        if (member == null) return null;

        Long groupId = member.getId().getGroupId();
        Long memberId = member.getId().getMemberId(); // users.id

        String userName = null;
        if (user != null) {
            // User 엔티티 필드 맞춰서 선택
            userName = user.getNickname();  // 또는 getName()
        }

        String role = "MEMBER";
        if (leaderId != null && leaderId.equals(memberId)) {
            role = "LEADER";
        }

        return StudyGroupMemberSummary.builder()
                .groupMemberId(memberId)  // 프론트에서는 사실상 userId랑 동일하게 씀
                .userId(memberId)
                .userName(userName)
                .role(role)
                .build();
    }
}
