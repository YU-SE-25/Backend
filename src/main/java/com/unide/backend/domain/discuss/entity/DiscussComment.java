package com.unide.backend.domain.discuss.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "discuss_comment")
public class DiscussComment {

    // 댓글 ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    // 댓글이 달린 게시물 ID
    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 댓글 작성자 ID
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    // 익명 여부
    @Column(name = "is_anonymous", nullable = false)
    private boolean anonymous;

    // 부모 댓글 ID (대댓글용) – 최상위 댓글은 null
    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    // 댓글 내용
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 좋아요 수
    @Column(name = "like_count", nullable = false)
    private int likeCount;

    // 댓글 공개 여부 (비공개도 포함)
    @Column(name = "is_private", nullable = false)
    private boolean privatePost;

    // 작성 시각
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정 시각
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
