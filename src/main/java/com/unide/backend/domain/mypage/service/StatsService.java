package com.unide.backend.domain.mypage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.mypage.dto.UserStatsResponseDto;
import com.unide.backend.domain.mypage.entity.Stats;
import com.unide.backend.domain.mypage.repository.StatsRepository;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final StatsRepository statsRepository;
    private final UserRepository userRepository;
    private final SubmissionsRepository submissionsRepository;

    /** 통계 조회 */
    public UserStatsResponseDto getStats(Long userId) {
        Stats stats = statsRepository.findByUserId(userId);

        if (stats == null) {
            return UserStatsResponseDto.builder()
                    .totalSolved(0)
                    .totalSubmitted(0)
                    .acceptanceRate(0.0)
                    .streakDays(0)
                    .ranking(0)
                    .rating(0)
                    .build();
        }

        return UserStatsResponseDto.builder()
                .totalSolved(stats.getTotalSolved())
                .totalSubmitted(stats.getTotalSubmitted())
                .acceptanceRate(stats.getAcceptanceRate())
                .streakDays(stats.getStreakDays())
                .ranking(stats.getRanking())
                .rating(stats.getRating())
                .build();
    }

    /** 자동 통계 업데이트 (제출/정답/스트릭/랭킹만 갱신, rating은 건드리지 않음) */
    @Transactional
    public void updateStats(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 1) 총 제출 수
        long totalSubmitted = submissionsRepository.countByUserId(userId);

        // 2) 총 해결한 문제 수
        long totalSolved = submissionsRepository.countSolvedByUserId(userId);

        // 3) 정답률
        double acceptanceRate =
                totalSubmitted > 0 ? (double) totalSolved / totalSubmitted * 100 : 0.0;

        // 4) 스트릭 계산
        int streakDays = calculateStreakDays(userId);

        // 5) 랭킹 계산 (향후 구현)
        int ranking = 0;

        // 기존 데이터 조회
        Stats stats = statsRepository.findByUserId(userId);

        if (stats == null) {
            stats = Stats.builder()
                    .user(user)
                    .totalSolved((int) totalSolved)
                    .totalSubmitted((int) totalSubmitted)
                    .acceptanceRate(acceptanceRate)
                    .streakDays(streakDays)
                    .ranking(ranking)
                    .rating(0) // 초기 rating 0, 이벤트로만 변화
                    .build();
        } else {
            stats.updateTotalSolved((int) totalSolved);
            stats.updateTotalSubmitted((int) totalSubmitted);
            stats.updateAcceptanceRate(acceptanceRate);
            stats.updateStreakDays(streakDays);
            stats.updateRanking(ranking);
            // rating은 여기서 수정 X
        }

        statsRepository.save(stats);
    }

    /** streak 계산 로직: 최근부터 연속적으로 제출한 날짜 수 계산 */
    private int calculateStreakDays(Long userId) {
        var submissions = submissionsRepository.findAllByUserIdOrderBySubmittedAtDesc(userId);
        if (submissions == null || submissions.isEmpty()) return 0;

        java.time.LocalDate today = java.time.LocalDate.now();
        int streak = 0;
        java.util.Set<java.time.LocalDate> submittedDates = new java.util.HashSet<>();
        for (var sub : submissions) {
            if (sub.getSubmittedAt() != null) {
                submittedDates.add(sub.getSubmittedAt().toLocalDate());
            }
        }

        // 오늘부터 과거로 연속적으로 제출한 날짜 수 계산
        java.time.LocalDate date = today;
        while (submittedDates.contains(date)) {
            streak++;
            date = date.minusDays(1);
        }
        return streak;
    }

    /** 공통: rating 증감 메서드 */
    @Transactional
    public void addRating(Long userId, int delta) {
        Stats stats = statsRepository.findByUserId(userId);

        if (stats == null) {
            // Stats가 아직 없으면 새로 생성
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

            stats = Stats.builder()
                    .user(user)
                    .totalSolved(0)
                    .totalSubmitted(0)
                    .acceptanceRate(0.0)
                    .streakDays(0)
                    .ranking(0)
                    .rating(delta)   // 첫 이벤트 점수로 시작
                    .build();
        } else {
            int current = stats.getRating() == null ? 0 : stats.getRating();
            stats.updateRating(current + delta);
        }

        statsRepository.save(stats);
    }

    // =========================
    //   이벤트별 평판 점수 로직
    // =========================

    // 1. 내가 쓴 토론 게시물에 좋아요 한 번 눌릴 때 마다 +2점씩
    @Transactional
    public void onDiscussPostLiked(Long authorId) {
        addRating(authorId, 2);
    }

    // 2. 내가 쓴 qna게시물에 좋아요 한 번 마다 +3씩
    @Transactional
    public void onQnaPostLiked(Long authorId) {
        addRating(authorId, 3);
    }

    // 3. 내가 쓴 토론 게시물에 단 댓글에 좋아요 한 번 마다 +1
    @Transactional
    public void onDiscussCommentLiked(Long authorId) {
        addRating(authorId, 1);
    }

    // 4. 내가 쓴 Qna 게시물에 단 댓글에 좋아요 한 번 마다 +2
    @Transactional
    public void onQnaCommentLiked(Long authorId) {
        addRating(authorId, 2);
    }

    // 5. 내가 쓴 review에 좋아요 마다 +5
    @Transactional
    public void onReviewLiked(Long authorId) {
        addRating(authorId, 5);
    }

    // 6. review 댓글에 달린 좋아요 마다 +3
    @Transactional
    public void onReviewCommentLiked(Long authorId) {
        addRating(authorId, 3);
    }

    // 7. 토론게시판 댓글 한 번 달릴 때마다 +2
    @Transactional
    public void onDiscussCommentCreated(Long writerId) {
        addRating(writerId, 2);
    }

    // 8. qna 게시판 댓글 한 번 달릴 때마다 +3
    @Transactional
    public void onQnaCommentCreated(Long writerId) {
        addRating(writerId, 3);
    }

    // 9. review 댓글 한 번 달릴 때마다 +4
    @Transactional
    public void onReviewCommentCreated(Long writerId) {
        addRating(writerId, 4);
    }

    // 10. 한 번 게시글 신고 당할 때마다 -10
    @Transactional
    public void onPostReported(Long authorId) {
        addRating(authorId, -10);
    }

    // 11. 코드 제출 한 번 할 때마다 +10
    @Transactional
    public void onCodeSubmitted(Long userId) {
        addRating(userId, 10);
    }
}
