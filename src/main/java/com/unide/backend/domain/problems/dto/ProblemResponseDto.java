// 문제 응답 DTO (목록 조회용)

package com.unide.backend.domain.problems.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.entity.ProblemTag;
import com.unide.backend.domain.problems.entity.Problems;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemResponseDto {
    private Long problemId;
    private String title;
    private List<ProblemTag> tags;
    private ProblemDifficulty difficulty;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private Boolean isSolved; // 사용자가 푼 문제인지 여부
    
    public static ProblemResponseDto from(Problems problem) {
        return ProblemResponseDto.builder()
                .problemId(problem.getId())
                .title(problem.getTitle())
                .tags(problem.getTags())
                .difficulty(problem.getDifficulty())
                .viewCount(problem.getViewCount())
                .createdAt(problem.getCreatedAt())
                .build();
    }
    
    public static ProblemResponseDto from(Problems problem, Boolean isSolved) {
        return ProblemResponseDto.builder()
                .problemId(problem.getId())
                .title(problem.getTitle())
                .tags(problem.getTags())
                .difficulty(problem.getDifficulty())
                .viewCount(problem.getViewCount())
                .createdAt(problem.getCreatedAt())
                .isSolved(isSolved)
                .build();
    }
}
