package com.unide.backend.domain.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexityAnalysisResponseDto {
    private String timeComplexity;
    private String timeReason;
    private String spaceComplexity;
    private String spaceReason;
}
