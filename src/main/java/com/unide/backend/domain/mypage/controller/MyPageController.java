package com.unide.backend.domain.mypage.controller;

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

import com.unide.backend.common.response.MessageResponseDto;
import com.unide.backend.domain.mypage.dto.MyPageResponseDto;
import com.unide.backend.domain.mypage.dto.MyPageUpdateRequestDto;
import com.unide.backend.domain.mypage.service.MyPageService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/{nickname}")
    public ResponseEntity<MyPageResponseDto> getMyPageByNickname(
            @PathVariable String nickname,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long requestUserId = principalDetails != null ? principalDetails.getUser().getId() : null;
        MyPageResponseDto response = myPageService.getMyPageByNickname(nickname, requestUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<MyPageResponseDto> getMyPage(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        MyPageResponseDto response = myPageService.getMyPage(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<MessageResponseDto> updateMyPage(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody MyPageUpdateRequestDto requestDto) {
        Long userId = principalDetails.getUser().getId();
        myPageService.updateMyPage(userId, requestDto);
        return ResponseEntity.ok(new MessageResponseDto("마이페이지가 성공적으로 수정되었습니다."));
    }

    @DeleteMapping
    public ResponseEntity<MessageResponseDto> deleteMyPage(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        myPageService.deleteMyPage(userId);
        return ResponseEntity.ok(new MessageResponseDto("마이페이지가 삭제되었습니다."));
    }

    @PostMapping("/initialize")
    public ResponseEntity<MessageResponseDto> initializeMyPage(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        myPageService.initializeMyPage(userId);
        return ResponseEntity.ok(new MessageResponseDto("마이페이지가 시작되었습니다."));
    }
}
