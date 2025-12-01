// 공유된 풀이 목록의 각 항목 정보를 담는 DTO

package com.unide.backend.domain.submissions.dto;

import java.time.LocalDateTime;

import com.unide.backend.domain.submissions.entity.SubmissionLanguage;
import com.unide.backend.domain.submissions.entity.SubmissionStatus;

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
public class SubmissionSolutionDto {
    private Long submissionId;
    private Long userId;
    private String nickname; // 작성자 닉네임
    private SubmissionLanguage language;
    private SubmissionStatus status;
    private Integer runtime;
    private Integer memory;
    private LocalDateTime submittedAt;

    
    // ===== 효율 관련 추가 필드 =====
    // 이 제출물의 리뷰 좋아요 총 개수
    private Integer totalVotes;

    // 효율 점수 (지금은 = totalVotes)
    private Double efficiencyScore;

    // 효율 순위 (1등, 2등, ...)
    private Long efficiencyRank;
}
