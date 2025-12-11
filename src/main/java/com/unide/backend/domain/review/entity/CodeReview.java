// 코드 리뷰 정보를 관리하는 엔터티

package com.unide.backend.domain.review.entity;

import com.unide.backend.common.entity.BaseTimeEntity;
import com.unide.backend.domain.submissions.entity.Submissions;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.review.entity.CodeReviewVote;

import java.util.List;
import java.util.ArrayList;

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
    
    @Column(name = "line_number")
    private Integer lineNumber;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodeReviewVote> votes;

    @Builder
    public CodeReview(Submissions submission, User reviewer, String content, Integer lineNumber) {
        this.submission = submission;
        this.reviewer = reviewer;
        this.content = content;
        this.lineNumber = lineNumber;
        this.voteCount = 0;
        this.votes = new ArrayList<>();
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
