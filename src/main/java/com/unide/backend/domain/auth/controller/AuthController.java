// 인증 관련 API 요청을 처리하는 컨트롤러

package com.unide.backend.domain.auth.controller;

import com.unide.backend.common.response.MessageResponseDto;
import com.unide.backend.domain.auth.dto.*;
import com.unide.backend.domain.auth.service.AuthService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.net.URI;
import java.util.Map;

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

    @PostMapping("/email/send-link")
    public ResponseEntity<MessageResponseDto> sendVerificationEmail(@Valid @RequestBody EmailRequestDto requestDto) {
        authService.sendVerificationEmail(requestDto);
        return ResponseEntity.ok(new MessageResponseDto("인증 이메일이 발송되었습니다."));
    }

    @GetMapping("/email/verify-link")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        URI redirectUri = URI.create("http://localhost:5173/auth/verify-success");
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }

    @PostMapping("/email/send-welcome")
    public ResponseEntity<MessageResponseDto> sendWelcomeEmail(@Valid @RequestBody WelcomeEmailRequestDto requestDto) {
        authService.sendWelcomeEmail(requestDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new MessageResponseDto("환영 이메일 요청이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto response = authService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(@Valid @RequestBody TokenRefreshRequestDto requestDto) {
        String newAccessToken = authService.refreshToken(requestDto);
        TokenRefreshResponseDto response = TokenRefreshResponseDto.builder()
                .accessToken(newAccessToken)
                .expiresIn(3600L)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDto> logout(@Valid @RequestBody LogoutRequestDto requestDto) {
        authService.logout(requestDto);
        return ResponseEntity.ok(new MessageResponseDto("로그아웃이 성공적으로 처리되었습니다."));
    }

    @PostMapping("/password/send-reset-code")
    public ResponseEntity<MessageResponseDto> sendPasswordResetCode(@Valid @RequestBody EmailRequestDto requestDto) {
        authService.sendPasswordResetCode(requestDto);
        return ResponseEntity.ok(new MessageResponseDto("비밀번호 재설정 코드가 이메일로 발송되었습니다."));
    }

    @PostMapping("/password/verify-reset-code")
    public ResponseEntity<PasswordResetCodeVerifyResponseDto> verifyPasswordResetCode(@Valid @RequestBody PasswordResetCodeVerifyRequestDto requestDto) {
        PasswordResetCodeVerifyResponseDto response = authService.verifyPasswordResetCode(requestDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password/reset")
    public ResponseEntity<MessageResponseDto> resetPassword(@Valid @RequestBody PasswordResetRequestDto requestDto) {
        authService.resetPassword(requestDto);
        return ResponseEntity.ok(new MessageResponseDto("비밀번호가 성공적으로 재설정되었습니다."));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponseDto> withdraw(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody(required = false) WithdrawRequestDto requestDto) {
        
        WithdrawRequestDto dto = (requestDto != null) ? requestDto : new WithdrawRequestDto();
        
        authService.withdraw(principalDetails.getUser(), dto);
        return ResponseEntity.ok(new MessageResponseDto("회원 탈퇴가 완료되었습니다."));
    }
}
