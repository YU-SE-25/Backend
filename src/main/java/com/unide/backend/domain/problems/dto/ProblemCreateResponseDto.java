// 문제 등록/수정 응답 DTO

package com.unide.backend.domain.problems.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProblemCreateResponseDto {
    private String message;
    private Long problemId;
    private LocalDateTime timestamp;
    
    public static ProblemCreateResponseDto of(String message, Long problemId) {
        return ProblemCreateResponseDto.builder()
                .message(message)
                .problemId(problemId)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
