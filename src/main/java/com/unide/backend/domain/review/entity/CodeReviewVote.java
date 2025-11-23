// 리뷰 투표 정보를 관리하는 엔터티

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
@Table(name = "code_review_vote", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"review_id", "voter_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CodeReviewVote extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private CodeReview review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private User voter;

    @Builder
    public CodeReviewVote(CodeReview review, User voter) {
        this.review = review;
        this.voter = voter;
    }
}
