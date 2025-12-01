package com.unide.backend.domain.efficiency.dto;

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
public class SubmissionEfficiencyDto {

    private Long submissionId;

    private Long authorId;
    private String authorName;

    // 이 제출물에 달린 리뷰 좋아요 총 개수
    private int totalVotes;

    // 정렬에 사용할 효율 점수 (지금은 = totalVotes)
    private double efficiencyScore;

    // 효율 랭킹 (1등, 2등, ...)
    private Long rank;
}
