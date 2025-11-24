package com.unide.backend.domain.qna.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.qna.entity.QnAComment;

public interface QnACommentRepository extends JpaRepository<QnAComment, Long> {

    // 특정 게시글의 전체 댓글 (대댓글까지) 시간순
    List<QnAComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // 특정 게시글의 최상위 댓글만
    List<QnAComment> findByPostIdAndParentCommentIdIsNullOrderByCreatedAtAsc(Long postId);

    // 특정 댓글의 대댓글 목록
    List<QnAComment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);
}
