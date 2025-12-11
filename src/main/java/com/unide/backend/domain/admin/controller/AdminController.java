// 관리자 기능 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.admin.controller;

import com.unide.backend.domain.admin.service.AdminService;
import com.unide.backend.domain.admin.dto.RoleChangeRequestDto;
import com.unide.backend.domain.admin.dto.RoleChangeResponseDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationDetailDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationListResponseDto;
import com.unide.backend.domain.admin.dto.InstructorApplicationUpdateRequestDto;
import com.unide.backend.domain.admin.dto.UserListResponseDto;
import com.unide.backend.domain.admin.dto.BlacklistListResponseDto;
import com.unide.backend.domain.admin.dto.BlacklistCreateRequestDto;
import com.unide.backend.domain.admin.dto.BlacklistCreateResponseDto;
import com.unide.backend.domain.admin.dto.BlacklistDeleteResponseDto;
import com.unide.backend.global.security.auth.PrincipalDetails;
import com.unide.backend.domain.auth.dto.InstructorApproveEmailRequestDto;
import com.unide.backend.domain.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final AuthService authService;

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

    @GetMapping("/users")
    public ResponseEntity<UserListResponseDto> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        
        UserListResponseDto response = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/blacklist")
    public ResponseEntity<BlacklistListResponseDto> getBlacklist(
            @PageableDefault(size = 20, sort = "bannedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        BlacklistListResponseDto response = adminService.getBlacklist(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/blacklist")
    public ResponseEntity<BlacklistCreateResponseDto> addToBlacklist(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody BlacklistCreateRequestDto requestDto) {
        
        BlacklistCreateResponseDto response = adminService.addToBlacklist(principalDetails.getUser(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/blacklist/{blacklistId}")
    public ResponseEntity<BlacklistDeleteResponseDto> removeFromBlacklist(@PathVariable Long blacklistId) {
        BlacklistDeleteResponseDto response = adminService.removeFromBlacklist(blacklistId);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/approve-instructor-email") 
    public ResponseEntity<Void> sendInstructorApprovalEmail(
        @RequestBody InstructorApproveEmailRequestDto requestDto) {
        authService.sendInstructorApprovalEmail(requestDto);
        
        return ResponseEntity.ok().build();
    }
}
