package com.unide.backend.domain.bookmark.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.bookmark.service.BookmarkService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookmark/problems")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    /** 문제 북마크 토글 (등록/취소) */
    @PostMapping("/{problemId}")
    public ResponseEntity<String> toggleBookmark(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long problemId) {
        Long userId = principalDetails.getUser().getId();
        bookmarkService.toggleBookmark(userId, problemId);
        return ResponseEntity.ok("북마크 상태가 변경되었습니다.");
    }
}