// 코드 제출 및 실행 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.submissions.controller;

import com.unide.backend.domain.submissions.dto.CodeDraftSaveRequestDto;
import com.unide.backend.domain.submissions.dto.CodeDraftSaveResponseDto;
import com.unide.backend.domain.submissions.dto.CodeDraftResponseDto;
import com.unide.backend.domain.submissions.service.SubmissionService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam; 

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/submissions")
public class SubmissionController {
    private final SubmissionService submissionService;

    @PatchMapping("/draft")
    public ResponseEntity<CodeDraftSaveResponseDto> saveCodeDraft(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody CodeDraftSaveRequestDto requestDto) {
        
        CodeDraftSaveResponseDto response = submissionService.saveCodeDraft(principalDetails.getUser(), requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/draft")
    public ResponseEntity<CodeDraftResponseDto> getDraftCode(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam Long problemId) {
        
        CodeDraftResponseDto response = submissionService.getDraftCode(principalDetails.getUser(), problemId);
        return ResponseEntity.ok(response);
    }
}