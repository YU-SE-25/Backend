// CodeReviewVote 엔터티에 대한 데이터베이스 접근을 처리하는 JpaRepository

package com.unide.backend.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.review.entity.CodeReviewVote;
import com.unide.backend.domain.user.entity.User;

public interface CodeReviewVoteRepository extends JpaRepository<CodeReviewVote, Long> {
    Optional<CodeReviewVote> findByReviewAndVoter(CodeReview review, User voter);

    
    /**
     * submissionIds 목록에 대해,
     * 각 제출물(submission_id) 별 review 좋아요(vote) 총 개수를 구하는 쿼리
     *
     * 반환:
     *  row[0] : submissionId (Long)
     *  row[1] : voteCount   (Long)
     */
    @Query("""
           SELECT v.review.submission.id AS submissionId,
                  COUNT(v)               AS voteCount
           FROM CodeReviewVote v
           WHERE v.review.submission.id IN :submissionIds
           GROUP BY v.review.submission.id
           """)
    List<Object[]> countVotesBySubmissionIds(@Param("submissionIds") List<Long> submissionIds);
}
