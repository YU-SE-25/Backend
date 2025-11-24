// 코드 제출 및 채점 결과 응답 DTO

package com.unide.backend.domain.submissions.dto;

import com.unide.backend.domain.submissions.entity.SubmissionStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubmissionResponseDto {
    private Long submissionId;
    private SubmissionStatus status; // 채점 결과 (CA, WA, TLE 등)
    private Integer runtime;        // 실행 시간 (ms)
    private Integer memory;         // 메모리 사용량 (KB)
    private Integer passedTestCases;
    private Integer totalTestCases;
    private String compileOutput;   // 컴파일 에러 메시지 (있을 경우)
    private String message;
}
