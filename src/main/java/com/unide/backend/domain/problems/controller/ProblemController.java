// 문제 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.problems.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.unide.backend.common.response.MessageResponseDto;
import com.unide.backend.domain.problems.dto.ProblemCreateRequestDto;
import com.unide.backend.domain.problems.dto.ProblemCreateResponseDto;
import com.unide.backend.domain.problems.dto.ProblemDetailResponseDto;
import com.unide.backend.domain.problems.dto.ProblemResponseDto;
import com.unide.backend.domain.problems.dto.ProblemUpdateRequestDto;
import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.service.ProblemService;
import com.unide.backend.domain.submissions.dto.LongestTimeResponseDto;
import com.unide.backend.domain.submissions.service.SubmissionService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problems")
public class ProblemController {
    private final ProblemService problemService;
    private final SubmissionService submissionService;

    /**등록 문제 조회 (매니저용) */
    @GetMapping("/list/pending")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Page<ProblemResponseDto>> getPendingProblems(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProblemResponseDto> problems = problemService.getProblemsByStatus(com.unide.backend.domain.problems.entity.ProblemStatus.PENDING, pageable);
        return ResponseEntity.ok(problems);
    }

    /** 문제 태그 조회 */
    @GetMapping("/tags")
    public ResponseEntity<List<String>> getProblemTags() {
        List<String> tags = new java.util.ArrayList<>();
        for (com.unide.backend.domain.problems.entity.ProblemTag tag : com.unide.backend.domain.problems.entity.ProblemTag.values()) {
            tags.add(tag.name());
        }
        return ResponseEntity.ok(tags);
    }
    
    /** 내가 만든 문제 조회 */
    @GetMapping("/list/me")
    public ResponseEntity<Page<ProblemResponseDto>> getMyProblems(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = principalDetails.getUser().getId();
        Page<ProblemResponseDto> problems = problemService.getProblemsByCreator(userId, pageable);
        return ResponseEntity.ok(problems);
    }
    
    /** 문제 등록 */
        @PostMapping("/register")
        @PreAuthorize("hasAnyRole('MANAGER', 'INSTRUCTOR')")
        public ResponseEntity<ProblemCreateResponseDto> createProblem(
                @AuthenticationPrincipal PrincipalDetails principalDetails,
                @Valid @ModelAttribute ProblemCreateRequestDto requestDto) {
            Long problemId = problemService.createProblem(principalDetails.getUser(), requestDto);
            return ResponseEntity.ok(ProblemCreateResponseDto.of("문제가 성공적으로 등록되었습니다.", problemId));
        }
    
    /** 문제 수정 */
    @PutMapping("/{problemId}")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<ProblemCreateResponseDto> updateProblem(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long problemId,
            @Valid @RequestBody ProblemUpdateRequestDto requestDto) {
        problemService.updateProblem(principalDetails.getUser(), problemId, requestDto);
        return ResponseEntity.ok(ProblemCreateResponseDto.of("문제가 성공적으로 수정되었습니다.", problemId));
    }
    
    /** 문제 리스트 조회 (태그 검색 포함) */
    @GetMapping("/list")
    public ResponseEntity<Page<ProblemResponseDto>> getProblems(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) ProblemDifficulty difficulty,
            @RequestParam(required = false) List<String> tags,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = principalDetails != null ? principalDetails.getUser().getId() : null;
        Page<ProblemResponseDto> problems;
        if ((tags != null && !tags.isEmpty()) || title != null || difficulty != null) {
            problems = problemService.searchProblems(userId, title, difficulty, tags, pageable);
        } else {
            problems = problemService.getProblems(userId, pageable);
        }
        return ResponseEntity.ok(problems);
    }
    
    /** 문제 상세 조회 */
    @GetMapping("/detail/{problemId}")
    public ResponseEntity<ProblemDetailResponseDto> getProblemDetail(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long problemId) {
        ProblemDetailResponseDto response = problemService.getProblemDetail(problemId, principalDetails);
        return ResponseEntity.ok(response);
    }
    
    /** 문제 삭제 */
    @DeleteMapping("/{problemId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponseDto> deleteProblem(@PathVariable Long problemId) {
        problemService.deleteProblem(problemId);
        return ResponseEntity.ok(new MessageResponseDto("문제가 삭제되었습니다."));
    }

    /** 문제 삭제 권한 검증 */
    @GetMapping("/{problemId}/stats/longest-time")
    public ResponseEntity<LongestTimeResponseDto> getLongestRuntime(
            @PathVariable Long problemId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        LongestTimeResponseDto response = submissionService.getLongestRuntime(principalDetails.getUser(), problemId);
        return ResponseEntity.ok(response);
    }
}