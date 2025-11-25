package com.unide.backend.domain.discuss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.discuss.entity.DiscussComment;

public interface DiscussCommentRepository extends JpaRepository<DiscussComment, Long> {

    // 특정 게시글의 전체 댓글 (대댓글까지) 시간순
    List<DiscussComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // 특정 게시글의 최상위 댓글만
    List<DiscussComment> findByPostIdAndParentCommentIdIsNullOrderByCreatedAtAsc(Long postId);

    // 특정 댓글의 대댓글 목록
    List<DiscussComment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);
}
