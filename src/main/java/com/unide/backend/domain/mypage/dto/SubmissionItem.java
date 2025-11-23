package com.unide.backend.domain.mypage.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubmissionItem {
    private final Long submissionId;
    private final Long problemId;
    private final String verdict;
    private final String language;
    private final int runtimeMs;
    private final LocalDateTime submittedAt;
}
