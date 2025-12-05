package com.unide.backend.domain.mainpage.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.mainpage.dto.CodeReviewRankDto;
import com.unide.backend.domain.mainpage.dto.MainProblemViewRankDto;
import com.unide.backend.domain.mainpage.entity.ReputationEvent;
import com.unide.backend.domain.mainpage.repository.CodeReviewRankProjection;
import com.unide.backend.domain.mainpage.repository.CodeReviewRankRepository;
import com.unide.backend.domain.mainpage.repository.MainProblemViewRankRepository;
import com.unide.backend.domain.mainpage.repository.ReputationRankProjection;
import com.unide.backend.domain.mainpage.repository.ReputationRankRepository;
import com.unide.backend.domain.mypage.dto.UserStatsResponseDto;
import com.unide.backend.domain.mypage.entity.Stats;
import com.unide.backend.domain.mypage.repository.StatsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainpageService {

    // 문제 조회수 랭킹용
    private final MainProblemViewRankRepository mainProblemViewRankRepository;

    // 사용자 평판 랭킹용 (reputation_events)
    private final ReputationRankRepository reputationRankRepository;

    // 코드 리뷰 랭킹용 (code_review)
    private final CodeReviewRankRepository codeReviewRankRepository;
    private final StatsRepository statsRepository;

    // ===========================
    // 1. 주간 문제 조회수 순위
    // ===========================
    public List<MainProblemViewRankDto> getProblemViewList(LocalDate rankDate) {
        // 1. 현재 주간 순위 목록 조회
        List<MainProblemViewRankDto> currentBaseList =
        mainProblemViewRankRepository.findRankedProblemsByDate(rankDate);
        // 2. 이전 주간 순위 조회 기준일 계산
        LocalDate previousDate = rankDate.minusWeeks(1);

        // 3. 이전 주간 순위 목록 조회
        List<MainProblemViewRankDto> previousBaseList =
                mainProblemViewRankRepository.findRankedProblemsByDate(previousDate);

        // 4. 이전 순위를 문제 ID를 키로 하는 Map<문제 ID, 순위> 형태로 변환
        Map<Long, Integer> previousRankMap = createProblemRankMap(previousBaseList);

        // 5. 현재 순위 및 Delta 계산
        List<MainProblemViewRankDto> finalRankList = new ArrayList<>();

        for (int i = 0; i < currentBaseList.size(); i++) {
            MainProblemViewRankDto currentDto = currentBaseList.get(i);
            int currentRank = i + 1;

            Integer previousRank = previousRankMap.get(currentDto.getProblemId());
            int delta = 0;

            if (previousRank != null) {
                // Delta 계산: (이전 순위 - 현재 순위). 양수: 상승, 음수: 하락
                delta = previousRank - currentRank;
            }

            // 최종 DTO 생성 및 목록에 추가
finalRankList.add(new MainProblemViewRankDto(currentDto, currentRank, delta));
        }

        return finalRankList;
    }

    private Map<Long, Integer> createProblemRankMap(List<MainProblemViewRankDto> rankedList) {
        Map<Long, Integer> rankMap = new HashMap<>();
        for (int i = 0; i < rankedList.size(); i++) {
            rankMap.put(rankedList.get(i).getProblemId(), i + 1);
        }
        return rankMap;
    }

    // ===========================
    // 2. 사용자 평판 랭킹 (UserStats)
    // ===========================
    @Transactional(readOnly = true)
    public List<UserStatsResponseDto> getReputationRankList(int size) {

        List<ReputationRankProjection> rows =
                reputationRankRepository.findRankTop(size);

        return rows.stream()
                .map(r -> UserStatsResponseDto.builder()
                        .userId(r.getUserId())      // id도 그대로 보내고
                        .nickname(r.getNickname())  // ⭐ 닉네임 추가
                        .totalSolved(0)
                        .totalSubmitted(0)
                        .acceptanceRate(0)
                        .streakDays(0)
                        .ranking(r.getRanking())
                        .rating(r.getRating())
                        .delta(r.getDelta())
                        .build()
                )
                .collect(Collectors.toList());
    }

    // --순위변동 계산
    @Transactional
    public void updateWeeklyReputationRanking() {

        // 1. 전체 유저 평판 점수 순위 가져오기
        List<Stats> users = statsRepository.findAllByOrderByRatingDesc();

        int rank = 1;
        Integer previousRank = null;

        for (Stats s : users) {

            // Delta (순위 변화량)
            Integer delta = (previousRank == null) ? 0 : previousRank - rank;

            // 2. 이벤트 테이블에 저장
            ReputationEvent event = ReputationEvent.builder()
                    .user(s.getUser())
                    .ranking(rank)
                    .delta(delta)
                    .build();

            reputationRankRepository.save(event);

            previousRank = rank;
            rank++;
        }
    }

    // ===========================
    // 3. 코드 리뷰 순위 (vote_count 기준)
    // ===========================
   @Transactional(readOnly = true)
public List<CodeReviewRankDto> getCodeReviewRankList(int size) {

    // “최근 7일” 기준
    LocalDateTime to = LocalDate.now().plusDays(1).atStartOfDay();    // 내일 0시
    LocalDateTime from = to.minusDays(7);                             // 7일 전 0시

    List<CodeReviewRankProjection> rows =
            codeReviewRankRepository.findTopReviewsByVotes(from, to, size);

    List<CodeReviewRankDto> result = new ArrayList<>();

    for (int i = 0; i < rows.size(); i++) {
        CodeReviewRankProjection row = rows.get(i);

        int ranking = i + 1;   // ⭐ 현재 순위
        int delta = 0;         // ⭐ 일단 0으로 고정 (이전 주 데이터 없어서)

        result.add(
                CodeReviewRankDto.builder()
                        .reviewId(row.getId())
                        .authorId(row.getAuthorId())
                        .nickname(row.getNickname())
                        .ranking(ranking)
                        .delta(delta)
                        .vote(row.getVote())
                        .build()
        );
    }

    return result;
}
}
