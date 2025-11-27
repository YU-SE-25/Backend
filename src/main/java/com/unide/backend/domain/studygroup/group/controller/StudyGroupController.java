package com.unide.backend.domain.studygroup.group.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.studygroup.group.dto.StudyGroupCreateRequest;
import com.unide.backend.domain.studygroup.group.dto.StudyGroupDetailResponse;
import com.unide.backend.domain.studygroup.group.dto.StudyGroupListItemResponse;
import com.unide.backend.domain.studygroup.group.dto.StudyGroupUpdateRequest;
import com.unide.backend.domain.studygroup.group.service.StudyGroupService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studygroup")
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    // ===== 스터디 그룹 생성 =====
    // POST /api/studygroup
    @PostMapping
    public ResponseEntity<StudyGroupDetailResponse> createGroup(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody StudyGroupCreateRequest request
    ) {
        Long leaderId = userDetails.getUser().getId();
        StudyGroupDetailResponse response =
                studyGroupService.createGroup(leaderId, request);
        return ResponseEntity.ok(response);
    }

    // ===== 스터디 그룹 목록 조회 =====
    // GET /api/studygroup?pageSize=10
    @GetMapping
    public ResponseEntity<List<StudyGroupListItemResponse>> listGroups(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        Long currentUserId = (userDetails != null)
                ? userDetails.getUser().getId()
                : null;

        List<StudyGroupListItemResponse> list =
                studyGroupService.listGroups(currentUserId, pageSize);

        return ResponseEntity.ok(list);
    }

    // ===== 특정 그룹 상세 조회 =====
    // GET /api/studygroup/list/{group_id}
    @GetMapping("/list/{groupId}")
    public ResponseEntity<StudyGroupDetailResponse> getGroupDetail(
            @PathVariable("groupId") Long groupId
    ) {
        StudyGroupDetailResponse response =
                studyGroupService.getGroupDetail(groupId);
        return ResponseEntity.ok(response);
    }

    // ===== 그룹 수정 =====
    // PATCH /api/studygroup/list/{group_id}
    @PatchMapping("/list/{groupId}")
    public ResponseEntity<StudyGroupDetailResponse> updateGroup(
            @PathVariable("groupId") Long groupId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody StudyGroupUpdateRequest request
    ) {
        Long requesterId = userDetails.getUser().getId();
        StudyGroupDetailResponse response =
                studyGroupService.updateGroup(groupId, requesterId, request);
        return ResponseEntity.ok(response);
    }

    // ===== 그룹 삭제 =====
    // DELETE /api/studygroup/list/{group_id}
    @DeleteMapping("/list/{groupId}")
    public ResponseEntity<Map<String, String>> deleteGroup(
            @PathVariable("groupId") Long groupId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long requesterId = userDetails.getUser().getId();
        studyGroupService.deleteGroup(groupId, requesterId);

        return ResponseEntity.ok(
                Map.of("message", "그룹이 삭제되었습니다!")
        );
    }
}
