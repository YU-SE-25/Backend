// 문제 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.problems.controller;

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

import com.unide.backend.common.response.MessageResponseDto;
import com.unide.backend.domain.problems.dto.ProblemCreateRequestDto;
import com.unide.backend.domain.problems.dto.ProblemCreateResponseDto;
import com.unide.backend.domain.problems.dto.ProblemDetailResponseDto;
import com.unide.backend.domain.problems.dto.ProblemResponseDto;
import com.unide.backend.domain.problems.dto.ProblemUpdateRequestDto;
import com.unide.backend.domain.problems.entity.ProblemDifficulty;
import com.unide.backend.domain.problems.service.ProblemService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problems")
public class ProblemController {
    
    private final ProblemService problemService;
    
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('MANAGER', 'INSTRUCTOR')")
    public ResponseEntity<ProblemCreateResponseDto> createProblem(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody ProblemCreateRequestDto requestDto) {
        Long problemId = problemService.createProblem(principalDetails.getUser(), requestDto);
        return ResponseEntity.ok(ProblemCreateResponseDto.of("문제가 성공적으로 등록되었습니다.", problemId));
    }
    
    @PutMapping("/{problemId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'INSTRUCTOR')")
    public ResponseEntity<ProblemCreateResponseDto> updateProblem(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long problemId,
            @Valid @RequestBody ProblemUpdateRequestDto requestDto) {
        problemService.updateProblem(principalDetails.getUser(), problemId, requestDto);
        return ResponseEntity.ok(ProblemCreateResponseDto.of("문제가 성공적으로 수정되었습니다.", problemId));
    }
    
    @GetMapping("/list")
    public ResponseEntity<Page<ProblemResponseDto>> getProblems(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) ProblemDifficulty difficulty,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Long userId = principalDetails != null ? principalDetails.getUser().getId() : null;
        Page<ProblemResponseDto> problems;
        
        if (title != null || difficulty != null) {
            problems = problemService.searchProblems(userId, title, difficulty, pageable);
        } else {
            problems = problemService.getProblems(userId, pageable);
        }
        
        return ResponseEntity.ok(problems);
    }
    
    @GetMapping("/detail/{problemId}")
    public ResponseEntity<ProblemDetailResponseDto> getProblemDetail(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long problemId) {
        ProblemDetailResponseDto response = problemService.getProblemDetail(problemId, principalDetails);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{problemId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponseDto> deleteProblem(@PathVariable Long problemId) {
        problemService.deleteProblem(problemId);
        return ResponseEntity.ok(new MessageResponseDto("문제가 삭제되었습니다."));
    }
}
