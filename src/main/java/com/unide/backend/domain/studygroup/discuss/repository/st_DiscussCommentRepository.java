package com.unide.backend.domain.studygroup.discuss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussComment;
public interface st_DiscussCommentRepository extends JpaRepository<st_DiscussComment, Long> {

    // 특정 게시글의 전체 댓글 (대댓글까지) 시간순
    List<st_DiscussComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // 특정 게시글의 최상위 댓글만
    List<st_DiscussComment> findByPostIdAndParentCommentIdIsNullOrderByCreatedAtAsc(Long postId);

    // 특정 댓글의 대댓글 목록
    List<st_DiscussComment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);

}
