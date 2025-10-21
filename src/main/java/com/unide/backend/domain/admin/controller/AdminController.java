// 관리자 기능 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.admin.controller;

import com.unide.backend.domain.admin.service.AdminService;
import com.unide.backend.domain.admin.dto.RoleChangeRequestDto;
import com.unide.backend.domain.admin.dto.RoleChangeResponseDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationDetailDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationListResponseDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationUpdateRequestDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<RoleChangeResponseDto> changeUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody RoleChangeRequestDto requestDto) {
        RoleChangeResponseDto response = adminService.changeUserRole(userId, requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instructor/applications")
    public ResponseEntity<InstructorApplicationListResponseDto> getPendingApplications(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        InstructorApplicationListResponseDto response = adminService.getPendingApplications(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instructor/applications/{applicationId}")
    public ResponseEntity<InstructorApplicationDetailDto> getApplicationDetail(
            @PathVariable Long applicationId) {
        InstructorApplicationDetailDto response = adminService.getApplicationDetail(applicationId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/instructor/applications/{applicationId}/status")
    public ResponseEntity<InstructorApplicationDetailDto> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody InstructorApplicationUpdateRequestDto requestDto) {
        InstructorApplicationDetailDto response = adminService.updateApplicationStatus(applicationId, requestDto);
        return ResponseEntity.ok(response);
    }
}
