// 코드 리뷰 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.review.service;

import com.unide.backend.domain.review.dto.ReviewListResponseDto;
import com.unide.backend.domain.review.dto.ReviewSummaryDto;
import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.review.entity.CodeReviewVote;
import com.unide.backend.domain.review.repository.CodeReviewVoteRepository;
import com.unide.backend.domain.review.repository.CodeReviewRepository;
import com.unide.backend.domain.review.dto.ReviewCreateRequestDto;
import com.unide.backend.domain.review.dto.ReviewCreateResponseDto;
import com.unide.backend.domain.review.dto.ReviewUpdateRequestDto;
import com.unide.backend.domain.review.dto.ReviewUpdateResponseDto;
import com.unide.backend.domain.review.dto.ReviewDeleteResponseDto;
import com.unide.backend.domain.review.dto.ReviewVoteResponseDto;
import com.unide.backend.global.exception.AuthException;
import com.unide.backend.domain.submissions.entity.Submissions;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final CodeReviewRepository codeReviewRepository;
    private final SubmissionsRepository submissionsRepository;
    private final CodeReviewVoteRepository codeReviewVoteRepository;

    /**
     * 특정 제출 코드에 대한 리뷰 목록 조회
     * @param submissionId 제출 코드 ID
     * @param pageable 페이징 정보
     * @param currentUser 현재 로그인한 사용자 (본인 리뷰 확인용)
     * @return 페이징된 리뷰 목록
    */
    public ReviewListResponseDto getReviewList(Long submissionId, Pageable pageable, User currentUser) {
        Submissions submission = submissionsRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제출 코드입니다: " + submissionId));

        Page<CodeReview> reviewPage = codeReviewRepository.findBySubmission(submission, pageable);

        List<ReviewSummaryDto> reviews = reviewPage.getContent().stream()
                .map(review -> ReviewSummaryDto.builder()
                        .reviewId(review.getId())
                        .reviewer(review.getReviewer().getNickname())
                        .content(review.getContent())
                        .voteCount(review.getVoteCount())
                        .createdAt(review.getCreatedAt())
                        .isOwner(currentUser != null && review.getReviewer().getId().equals(currentUser.getId()))
                        .build())
                .collect(Collectors.toList());

        return ReviewListResponseDto.builder()
                .totalPages(reviewPage.getTotalPages())
                .currentPage(reviewPage.getNumber())
                .reviews(reviews)
                .build();
    }

    /**
     * 새로운 리뷰를 작성하는 메서드
     * @param user 작성자
     * @param requestDto 리뷰 작성 요청 정보
     * @return 성공 메시지
    */
    @Transactional
    public ReviewCreateResponseDto createReview(User user, ReviewCreateRequestDto requestDto) {
        Submissions submission = submissionsRepository.findById(requestDto.getSubmissionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제출 코드입니다: " + requestDto.getSubmissionId()));

        CodeReview review = CodeReview.builder()
                .submission(submission)
                .reviewer(user)
                .content(requestDto.getContent())
                .build();

        codeReviewRepository.save(review);

        return ReviewCreateResponseDto.builder()
                .message("리뷰가 성공적으로 등록되었습니다.")
                .build();
    }

    /**
     * 리뷰 내용을 수정하는 메서드
     * @param reviewId 수정할 리뷰 ID
     * @param user 요청한 사용자 (작성자 본인 확인용)
     * @param requestDto 수정할 내용
     * @return 수정 결과 DTO
    */
    @Transactional
    public ReviewUpdateResponseDto updateReview(Long reviewId, User user, ReviewUpdateRequestDto requestDto) {
        CodeReview review = codeReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다: " + reviewId));

        if (!review.getReviewer().getId().equals(user.getId())) {
            throw new AuthException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        review.updateContent(requestDto.getContent());

        return ReviewUpdateResponseDto.builder()
                .reviewId(review.getId())
                .message("리뷰가 성공적으로 수정되었습니다.")
                .build();
    }

    /**
     * 리뷰를 삭제하는 메서드
     * @param reviewId 삭제할 리뷰 ID
     * @param user 요청한 사용자 (작성자 본인 확인용)
     * @return 삭제 결과 DTO
     */
    @Transactional
    public ReviewDeleteResponseDto deleteReview(Long reviewId, User user) {
        CodeReview review = codeReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다: " + reviewId));

        if (!review.getReviewer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        codeReviewRepository.delete(review);

        return ReviewDeleteResponseDto.builder()
                .reviewId(reviewId)
                .message("리뷰가 성공적으로 삭제되었습니다.")
                .build();
    }

    /**
     * 리뷰 투표(좋아요)를 토글하는 메서드
     * @param reviewId 투표할 리뷰 ID
     * @param user 투표하는 사용자
     * @return 투표 결과 DTO
    */
    @Transactional
    public ReviewVoteResponseDto toggleVote(Long reviewId, User user) {
        CodeReview review = codeReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다: " + reviewId));

        Optional<CodeReviewVote> voteOptional = codeReviewVoteRepository.findByReviewAndVoter(review, user);
        boolean viewerLiked;
        String message;

        if (voteOptional.isPresent()) {
            codeReviewVoteRepository.delete(voteOptional.get());
            review.updateVoteCount(review.getVoteCount() - 1);
            viewerLiked = false;
            message = "리뷰 투표를 취소했습니다.";
        } else {
            CodeReviewVote vote = CodeReviewVote.builder()
                    .review(review)
                    .voter(user)
                    .build();
            codeReviewVoteRepository.save(vote);
            review.updateVoteCount(review.getVoteCount() + 1);
            viewerLiked = true;
            message = "리뷰에 투표했습니다.";
        }

        return ReviewVoteResponseDto.builder()
                .reviewId(review.getId())
                .voteCount(review.getVoteCount())
                .viewerLiked(viewerLiked)
                .message(message)
                .build();
    }
}
