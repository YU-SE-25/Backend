// 문제 상세 조회 응답 DTO

package com.unide.backend.domain.problems.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.entity.ProblemTag;
import com.unide.backend.domain.problems.entity.ProblemStatus;
import com.unide.backend.domain.problems.entity.Problems;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProblemDetailResponseDto {
    private Long problemId;
    private String createdByNickname;
    private String title;
    private String description;
    private String summary;
    private String inputOutputExample;
    private ProblemDifficulty difficulty;
    private Integer timeLimit;
    private Integer memoryLimit;
    private ProblemStatus status;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProblemTag> tags;
    private String hint;
    private String source;
    private Long totalSubmissions;
    private Long acceptedSubmissions;
    private Double acceptanceRate;
    private Boolean canEdit; // 수정 가능 여부
    
    public static ProblemDetailResponseDto from(Problems problem) {
        return ProblemDetailResponseDto.builder()
                .problemId(problem.getId())
                .createdByNickname(problem.getCreatedBy().getNickname())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .summary(problem.getSummary())
                .inputOutputExample(problem.getInputOutputExample())
                .difficulty(problem.getDifficulty())
                .timeLimit(problem.getTimeLimit())
                .memoryLimit(problem.getMemoryLimit())
                .status(problem.getStatus())
                .viewCount(problem.getViewCount())
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .tags(problem.getTags())
                .hint(problem.getHint())
                .source(problem.getSource())
                .totalSubmissions(0L)
                .acceptedSubmissions(0L)
                .acceptanceRate(0.0)
                .build();
    }
    
    public static ProblemDetailResponseDto from(Problems problem, Long totalSubmissions, Long acceptedSubmissions, Boolean canEdit) {
        double acceptanceRate = totalSubmissions > 0 
            ? (acceptedSubmissions * 100.0 / totalSubmissions) 
            : 0.0;

        if (acceptanceRate > 100.0) {
            acceptanceRate = 100.0;
        }
            
        return ProblemDetailResponseDto.builder()
                .problemId(problem.getId())
                .createdByNickname(problem.getCreatedBy().getNickname())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .summary(problem.getSummary())
                .inputOutputExample(problem.getInputOutputExample())
                .difficulty(problem.getDifficulty())
                .timeLimit(problem.getTimeLimit())
                .memoryLimit(problem.getMemoryLimit())
                .status(problem.getStatus())
                .viewCount(problem.getViewCount())
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .tags(problem.getTags())
                .hint(problem.getHint())
                .source(problem.getSource())
                .totalSubmissions(totalSubmissions)
                .acceptedSubmissions(acceptedSubmissions)
                .acceptanceRate(Math.round(acceptanceRate * 10) / 10.0)
                .canEdit(canEdit)
                .build();
    }
}
