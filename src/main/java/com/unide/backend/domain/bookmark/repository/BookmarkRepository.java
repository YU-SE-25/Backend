package com.unide.backend.domain.bookmark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unide.backend.domain.bookmark.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT b.problem.id FROM Bookmark b WHERE b.user.id = :userId")
    List<Long> findBookmarkedProblemIdsByUserId(@Param("userId") Long userId);
}
