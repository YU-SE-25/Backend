// 코드 실행 결과 응답 DTO

package com.unide.backend.domain.submissions.dto;

import com.unide.backend.domain.submissions.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodeRunResponseDto {
    private String output;           // 실행 결과 (표준 출력)
    private String error;            // 에러 메시지 (표준 에러)
    private long executionTimeMs;    // 실행 소요 시간
    private boolean isSuccess;       // 실행 성공 여부
    private SubmissionStatus status; // 실행 상태 (CA, TLE, RE, CE 등)
}
