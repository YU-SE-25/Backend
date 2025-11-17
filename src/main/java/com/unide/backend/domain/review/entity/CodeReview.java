// 코드 리뷰 정보를 관리하는 엔터티

package com.unide.backend.domain.review.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.submissions.entity.Submissions;
import com.unide.backend.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "code_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CodeReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submissions submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "vote_count", nullable = false)
    private int voteCount;

    @Builder
    public CodeReview(Submissions submission, User reviewer, String content) {
        this.submission = submission;
        this.reviewer = reviewer;
        this.content = content;
        this.voteCount = 0;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
