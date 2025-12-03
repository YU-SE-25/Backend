package com.unide.backend.domain.bookmark.controller;

import com.unide.backend.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmark")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    /** 문제 북마크 토글 (등록/취소) */
    @PostMapping("/{problemId}")
    public ResponseEntity<String> toggleBookmark(
            @RequestParam Long userId,
            @PathVariable Long problemId) {
        bookmarkService.toggleBookmark(userId, problemId);
        return ResponseEntity.ok("북마크 상태가 변경되었습니다.");
    }
}
