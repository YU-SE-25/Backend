package com.unide.backend.domain.studygroup.problem.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyGroupProblemListSummaryDto {

    private Long problemListId;    // 리스트 ID
    private String listTitle;      // 제목
    private LocalDate dueDate;     // 마감일
    private List<Long> problems;   // 문제 ID 리스트
}
