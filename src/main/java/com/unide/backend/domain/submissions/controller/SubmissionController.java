// 코드 제출 및 실행 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.submissions.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.submissions.dto.CodeDraftResponseDto;
import com.unide.backend.domain.submissions.dto.CodeDraftSaveRequestDto;
import com.unide.backend.domain.submissions.dto.CodeDraftSaveResponseDto;
import com.unide.backend.domain.submissions.dto.SubmissionDetailResponseDto;
import com.unide.backend.domain.submissions.dto.SubmissionHistoryListDto;
import com.unide.backend.domain.submissions.dto.SubmissionRequestDto;
import com.unide.backend.domain.submissions.dto.SubmissionResponseDto;
import com.unide.backend.domain.submissions.dto.SubmissionShareRequestDto;
import com.unide.backend.domain.submissions.dto.SubmissionShareResponseDto;
import com.unide.backend.domain.submissions.dto.SubmissionSolutionListDto;
import com.unide.backend.domain.submissions.service.SubmissionService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

    @PostMapping
    public ResponseEntity<SubmissionResponseDto> submitCode(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody SubmissionRequestDto requestDto) {
        
        SubmissionResponseDto response = submissionService.submitCode(principalDetails.getUser(), requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{submissionId}/details")
    public ResponseEntity<SubmissionDetailResponseDto> getSubmissionDetail(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        SubmissionDetailResponseDto response = submissionService.getSubmissionDetail(
                submissionId,
                principalDetails.getUser()
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{submissionId}/share")
    public ResponseEntity<SubmissionShareResponseDto> updateShareStatus(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody SubmissionShareRequestDto requestDto) {
        
        SubmissionShareResponseDto response = submissionService.updateShareStatus(
                submissionId,
                principalDetails.getUser(),
                requestDto
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SubmissionHistoryListDto> getSubmissionHistory(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(required = false) Long problemId, // 선택적 파라미터
            @PageableDefault(size = 20) Pageable pageable) {
        
        SubmissionHistoryListDto response = submissionService.getSubmissionHistory(
                principalDetails.getUser(), 
                problemId, 
                pageable
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{problemId}/solutions")
    public ResponseEntity<SubmissionSolutionListDto> getSharedSolutions(
            @PathVariable Long problemId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        SubmissionSolutionListDto response = submissionService.getSharedSolutions(problemId, pageable);
        return ResponseEntity.ok(response);
    }
}
