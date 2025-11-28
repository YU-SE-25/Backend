package com.unide.backend.domain.studygroup.member.dto;

import java.time.LocalDateTime;

import com.unide.backend.domain.studygroup.member.entity.StudyGroupLog;
import com.unide.backend.domain.user.entity.User;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupActivityItemResponse {

    private Long activityId;
    private String type;       // JOIN / LEAVE / KICK / PROBLEM_SUBMIT ...
    private Long userId;
    private String userName;
    private String description;
    private LocalDateTime createdAt;

    public static StudyGroupActivityItemResponse fromEntity(StudyGroupLog log) {
        User actor = log.getActorUser();   // StudyGroupLog의 actorUser 필드에 대한 Lombok getter

        return StudyGroupActivityItemResponse.builder()
                .activityId(log.getActivityId())
                .type(log.getActivityType().name())
                .userId(actor != null ? actor.getId() : null)
                .userName(actor != null ? actor.getName() : null)
                .description(log.getMessage())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
