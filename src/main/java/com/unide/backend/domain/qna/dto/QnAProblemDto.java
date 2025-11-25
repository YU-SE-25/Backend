package com.unide.backend.domain.qna.dto;

import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.entity.Problems;

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
public class QnAProblemDto {

    private Long problemId;
    private String title;
    private ProblemDifficulty difficulty;

    public static QnAProblemDto fromEntity(Problems problem) {
        if (problem == null) return null;

        return QnAProblemDto.builder()
                .problemId(problem.getId())        // ← Problems 엔티티 PK 이름이 id 라고 가정
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .build();
    }

    public Long getProblemId() {
        return problemId;
    }
    public String getTitle() {
        return title;
    }
    public ProblemDifficulty getDifficulty() {
        return difficulty;
    }
}
