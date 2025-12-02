package com.unide.backend.domain.submissions.dto;

import com.unide.backend.domain.submissions.entity.SubmissionStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubmissionRecordDto {
    private int testCaseIndex;
    private SubmissionStatus status;
    private int runtime;
    private int memory;
}
