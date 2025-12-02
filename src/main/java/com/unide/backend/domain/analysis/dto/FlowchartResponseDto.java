package com.unide.backend.domain.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowchartResponseDto {
    private String mermaidCode; // Mermaid.js 그래프 코드
}
