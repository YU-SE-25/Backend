// 코드 리뷰 댓글 정보를 관리하는 엔터티

package com.unide.backend.domain.review.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "code_review_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CodeReviewComment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private CodeReview review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commenter_id", nullable = false)
    private User commenter;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private CodeReviewComment parentComment;

    @Builder
    public CodeReviewComment(CodeReview review, User commenter, String content, CodeReviewComment parentComment) {
        this.review = review;
        this.commenter = commenter;
        this.content = content;
        this.parentComment = parentComment;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
