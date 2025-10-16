// 관리자 기능 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.admin.controller;

import com.unide.backend.domain.admin.dto.RoleChangeRequestDto;
import com.unide.backend.domain.admin.dto.RoleChangeResponseDto;
import com.unide.backend.domain.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
