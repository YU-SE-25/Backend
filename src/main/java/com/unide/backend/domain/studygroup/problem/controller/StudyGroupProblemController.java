package com.unide.backend.domain.studygroup.problem.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.studygroup.problem.dto.StudyGroupProblemListDetailDto;
import com.unide.backend.domain.studygroup.problem.dto.StudyGroupProblemListRequest;
import com.unide.backend.domain.studygroup.problem.dto.StudyGroupProblemListSummaryDto;
import com.unide.backend.domain.studygroup.problem.service.StudyGroupProblemService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studygroup/{groupId}/problem/lists")
public class StudyGroupProblemController {

    private final StudyGroupProblemService studyGroupProblemService;

    /**
     * 지정된 문제 목록 전체 조회
     * GET /api/studygroup/{groupId}/problem/lists
     */
    @GetMapping
    public ResponseEntity<List<StudyGroupProblemListSummaryDto>> getProblemLists(
            @PathVariable Long groupId
    ) {
        List<StudyGroupProblemListSummaryDto> response =
                studyGroupProblemService.getProblemLists(groupId);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 문제 리스트 조회
     * GET /api/studygroup/{groupId}/problem/lists/{problemListId}
     */
    @GetMapping("/{problemListId}")
    public ResponseEntity<StudyGroupProblemListDetailDto> getProblemList(
            @PathVariable Long groupId,
            @PathVariable Long problemListId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long userId = (userDetails != null) ? userDetails.getUser().getId() : null;

        StudyGroupProblemListDetailDto response =
                studyGroupProblemService.getProblemList(groupId, problemListId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 문제 리스트 생성
     * POST /api/studygroup/{groupId}/problem/lists
     */
    @PostMapping
    public ResponseEntity<StudyGroupProblemListDetailDto> createProblemList(
            @PathVariable Long groupId,
            @RequestBody StudyGroupProblemListRequest request
    ) {
        StudyGroupProblemListDetailDto response =
                studyGroupProblemService.createProblemList(groupId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 문제 리스트 수정
     * PUT /api/studygroup/{groupId}/problem/lists/{problemListId}
     */
    @PutMapping("/{problemListId}")
    public ResponseEntity<StudyGroupProblemListDetailDto> updateProblemList(
            @PathVariable Long groupId,
            @PathVariable Long problemListId,
            @RequestBody StudyGroupProblemListRequest request
    ) {
        StudyGroupProblemListDetailDto response =
                studyGroupProblemService.updateProblemList(groupId, problemListId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 문제 리스트 삭제
     * DELETE /api/studygroup/{groupId}/problem/lists/{problemListId}
     */
    @DeleteMapping("/{problemListId}")
    public ResponseEntity<Map<String, String>> deleteProblemList(
            @PathVariable Long groupId,
            @PathVariable Long problemListId
    ) {
        studyGroupProblemService.deleteProblemList(groupId, problemListId);
        return ResponseEntity.ok(Map.of("message", "Problem list deleted"));
    }
}
