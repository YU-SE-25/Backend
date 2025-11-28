package com.unide.backend.domain.mypage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.mypage.dto.UserStatsResponseDto;
import com.unide.backend.domain.mypage.entity.Stats;
import com.unide.backend.domain.mypage.repository.StatsRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;

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

    /** 자동 통계 업데이트 */
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

        // 6) rating 계산 (원한다면)
        int rating = calculateRating(totalSolved, streakDays);

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
                    .rating(rating)
                    .build();
        } else {
            stats.updateTotalSolved((int) totalSolved);
            stats.updateTotalSubmitted((int) totalSubmitted);
            stats.updateAcceptanceRate(acceptanceRate);
            stats.updateStreakDays(streakDays);
            stats.updateRanking(ranking);
            stats.updateRating(rating);
        }

        statsRepository.save(stats);
    }

    /** streak 계산 로직: 최근부터 연속적으로 제출한 날짜 수 계산 */
    private int calculateStreakDays(Long userId) {
        // 제출 기록에서 제출 날짜만 추출 (LocalDate로 변환)
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

    /** rating 계산 로직 */
    private int calculateRating(long solved, int streak) {
        return (int) (solved * 10 + streak * 3);
    }
}
