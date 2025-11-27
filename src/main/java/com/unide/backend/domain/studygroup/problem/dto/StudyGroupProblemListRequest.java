package com.unide.backend.domain.studygroup.problem.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyGroupProblemListRequest {

    private String listTitle;          // 리스트 제목
    private LocalDate dueDate;         // 마감 날짜
    private List<Long> problems;       // 문제 ID 목록 (problem_id)
}
