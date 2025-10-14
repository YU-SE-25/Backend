// 인증 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.auth.controller;

import com.unide.backend.domain.auth.dto.AvailabilityResponseDto;
import com.unide.backend.domain.auth.dto.EmailCheckRequestDto;
import com.unide.backend.domain.auth.dto.NicknameCheckRequestDto;
import com.unide.backend.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/check/email")
    public ResponseEntity<AvailabilityResponseDto> checkEmailAvailability(@Valid @RequestBody EmailCheckRequestDto requestDto) {
        AvailabilityResponseDto response = authService.checkEmailAvailability(requestDto.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check/nickname")
    public ResponseEntity<AvailabilityResponseDto> checkNicknameAvailability(@Valid @RequestBody NicknameCheckRequestDto requestDto) {
        AvailabilityResponseDto response = authService.checkNicknameAvailability(requestDto.getNickname());
        return ResponseEntity.ok(response);
    }
}
