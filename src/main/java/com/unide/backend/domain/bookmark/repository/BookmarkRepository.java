package com.unide.backend.domain.bookmark.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.unide.backend.domain.bookmark.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 특정 사용자가 북마크한 문제 ID 목록 조회
    @Query("SELECT b.problem.id FROM Bookmark b WHERE b.user.id = :userId")
    List<Long> findBookmarkedProblemIdsByUserId(@Param("userId") Long userId);

    // 추가된 메서드들
    Optional<Bookmark> findByUserIdAndProblemId(Long userId, Long problemId);

    // 중복 체크용 메서드
    boolean existsByUserIdAndProblemId(Long userId, Long problemId);
}
