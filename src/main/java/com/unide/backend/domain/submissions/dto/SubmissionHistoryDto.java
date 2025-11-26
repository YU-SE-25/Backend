// 내 제출 이력의 각 항목 정보를 담는 DTO

package com.unide.backend.domain.submissions.dto;

import com.unide.backend.domain.submissions.entity.SubmissionLanguage;
import com.unide.backend.domain.submissions.entity.SubmissionStatus;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class SubmissionHistoryDto {
    private Long submissionId;
    private Long problemId;
    private String problemTitle;
    private SubmissionStatus status; // 채점 결과 (CA, WA, TLE 등)
    private SubmissionLanguage language;
    private Integer runtime;
    private Integer memory;
    private LocalDateTime submittedAt;
}
