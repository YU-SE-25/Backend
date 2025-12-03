package com.unide.backend.domain.bookmark.service;

import com.unide.backend.domain.bookmark.entity.Bookmark;
import com.unide.backend.domain.bookmark.repository.BookmarkRepository;
import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ProblemsRepository problemsRepository;

    /** 문제 북마크 토글 (등록/취소) */
    @Transactional
    public void toggleBookmark(Long userId, Long problemId) {
        if (bookmarkRepository.existsByUserIdAndProblemId(userId, problemId)) {
            // 이미 북마크된 경우: 북마크 취소
            Bookmark bookmark = bookmarkRepository.findByUserIdAndProblemId(userId, problemId)
                    .orElseThrow(() -> new IllegalArgumentException("북마크 내역 없음"));
            bookmarkRepository.delete(bookmark);
        } else {
            // 북마크가 없으면 등록
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
            Problems problem = problemsRepository.findById(problemId)
                    .orElseThrow(() -> new IllegalArgumentException("문제 없음"));
            Bookmark bookmark = new Bookmark(user, problem);
            bookmarkRepository.save(bookmark);
        }
    }

    /** 사용자가 북마크한 문제 ID 목록 조회 */
    @Transactional(readOnly = true)
    public List<Long> getBookmarkedProblemIds(Long userId) {
        return bookmarkRepository.findBookmarkedProblemIdsByUserId(userId);
    }
}
