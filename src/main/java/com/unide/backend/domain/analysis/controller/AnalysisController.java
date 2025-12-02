package com.unide.backend.domain.analysis.controller;

import com.unide.backend.domain.analysis.dto.HabitAnalysisResponseDto;
import com.unide.backend.domain.analysis.service.AnalysisService;
import com.unide.backend.global.security.auth.PrincipalDetails;
import com.unide.backend.domain.analysis.dto.ComplexityAnalysisResponseDto;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnalysisController {
    private final AnalysisService analysisService;

    @GetMapping("/analysis/habits")
    public ResponseEntity<HabitAnalysisResponseDto> analyzeHabits(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        HabitAnalysisResponseDto response = analysisService.analyzeCodingHabits(principalDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/submissions/{submissionId}/analysis/complexity")
    public ResponseEntity<ComplexityAnalysisResponseDto> analyzeComplexity(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        ComplexityAnalysisResponseDto response = analysisService.analyzeComplexity(
                submissionId, 
                principalDetails.getUser()
        );
        return ResponseEntity.ok(response);
    }
}
