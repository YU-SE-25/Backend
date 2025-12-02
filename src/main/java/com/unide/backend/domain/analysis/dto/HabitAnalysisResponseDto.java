package com.unide.backend.domain.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitAnalysisResponseDto {
    private String summary;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> suggestions;
}
