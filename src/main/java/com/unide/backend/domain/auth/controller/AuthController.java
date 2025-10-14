// 인증 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.auth.controller;

import com.unide.backend.domain.auth.dto.AvailabilityResponseDto;
import com.unide.backend.domain.auth.dto.EmailCheckRequestDto;
import com.unide.backend.domain.auth.dto.NicknameCheckRequestDto;
import com.unide.backend.domain.auth.dto.PhoneCheckRequestDto;
import com.unide.backend.domain.auth.dto.BlacklistCheckRequestDto;
import com.unide.backend.domain.auth.dto.BlacklistCheckResponseDto;
import com.unide.backend.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.unide.backend.domain.auth.dto.RegisterRequestDto;

import java.util.Map;

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

    @PostMapping("/check/phone")
    public ResponseEntity<AvailabilityResponseDto> checkPhoneAvailability(@Valid @RequestBody PhoneCheckRequestDto requestDto) {
        AvailabilityResponseDto response = authService.checkPhoneAvailability(requestDto.getPhone());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check/blacklist")
    public ResponseEntity<BlacklistCheckResponseDto> checkBlacklistStatus(@Valid @RequestBody BlacklistCheckRequestDto requestDto) {
        BlacklistCheckResponseDto response = authService.checkBlacklistStatus(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        Long userId = authService.registerUser(requestDto);
        Map<String, Object> responseBody = Map.of(
            "userId", userId,
            "message", "회원가입 완료"
        );
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

}
