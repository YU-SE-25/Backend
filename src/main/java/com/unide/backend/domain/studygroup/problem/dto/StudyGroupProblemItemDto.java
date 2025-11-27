package com.unide.backend.domain.studygroup.problem.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyGroupProblemItemDto {

    private Long problemId;      // 문제 ID
    private String problemTitle; // 문제 제목
    private String userStatus;   // SUBMITTED / NOT_SUBMITTED 등
}
