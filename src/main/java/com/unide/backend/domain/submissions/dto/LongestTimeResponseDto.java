// 가장 오래 걸린 실행 시간 응답 DTO

package com.unide.backend.domain.submissions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LongestTimeResponseDto {
    private Integer longestTimeMs;
}
