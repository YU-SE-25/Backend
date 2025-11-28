package com.unide.backend.domain.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.unide.backend.domain.mypage.dto.MyPageResponseDto;
import com.unide.backend.domain.mypage.dto.MyPageUpdateRequestDto;
import com.unide.backend.domain.mypage.dto.MyPageUpdateResponseDto;
import com.unide.backend.domain.mypage.service.MyPageService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /** 내 마이페이지 조회 */
    @GetMapping("/me")
    public ResponseEntity<MyPageResponseDto> getMyPage(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long userId = principal.getUser().getId();
        return ResponseEntity.ok(myPageService.getMyPage(userId));
    }

    /** 닉네임으로 마이페이지 조회 */
    @GetMapping("/{nickname}")
    public ResponseEntity<MyPageResponseDto> getMyPageByNickname(
            @PathVariable String nickname,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long requestUserId = (principal != null) ? principal.getUser().getId() : null;
        return ResponseEntity.ok(myPageService.getMyPageByNickname(nickname, requestUserId));
    }

    /** 내 프로필 업데이트 */
    @PatchMapping("/me")
    public ResponseEntity<MyPageUpdateResponseDto> updateMyPage(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody MyPageUpdateRequestDto requestDto) {
        Long userId = principalDetails.getUser().getId();
        MyPageResponseDto result = myPageService.updateMyPage(userId, requestDto);
        LocalDateTime updatedAt = result.getUpdatedAt(); // ISO 포맷 등
        return ResponseEntity.ok(new MyPageUpdateResponseDto("마이페이지가 성공적으로 수정되었습니다.", updatedAt));
    }

    @PostMapping("/me/initialize")
    public ResponseEntity<MyPageUpdateResponseDto> initializeMyPage(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        MyPageResponseDto result = myPageService.initializeMyPage(userId);
        LocalDateTime updatedAt = result.getUpdatedAt();
        return ResponseEntity.ok(new MyPageUpdateResponseDto("마이페이지가 초기화되었습니다.", updatedAt));
    }
    
}
