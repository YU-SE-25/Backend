// 코드 리뷰 관련 비즈니스 로직을 처리하는 서비스

package com.unide.backend.domain.review.service;

import com.unide.backend.domain.review.dto.ReviewListResponseDto;
import com.unide.backend.domain.review.dto.ReviewSummaryDto;
import com.unide.backend.domain.review.entity.CodeReview;
import com.unide.backend.domain.review.repository.CodeReviewRepository;
import com.unide.backend.domain.review.dto.ReviewCreateRequestDto;
import com.unide.backend.domain.review.dto.ReviewCreateResponseDto;
import com.unide.backend.domain.submissions.entity.Submissions;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final CodeReviewRepository codeReviewRepository;
    private final SubmissionsRepository submissionsRepository;

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
}
