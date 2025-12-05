// 제출 상세 정보 응답 DTO

package com.unide.backend.domain.submissions.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.unide.backend.domain.submissions.entity.SubmissionLanguage;
import com.unide.backend.domain.submissions.entity.SubmissionStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubmissionDetailResponseDto {
    private Long submissionId;
    private Long problemId;
    private String problemTitle;
    private String code;
    private SubmissionLanguage language;
    private SubmissionStatus status;
    private Integer runtime;
    private Integer memory;
    private LocalDateTime submittedAt;
    private boolean isShared;
    private Integer passedTestCases;
    private Integer totalTestCases;
    private List<SubmissionRecordDto> records;
}
