package com.unide.backend.domain.mainpage.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.mainpage.dto.CodeReviewRankDto;
import com.unide.backend.domain.mainpage.dto.MainProblemViewRankDto;
import com.unide.backend.domain.mainpage.service.MainpageService;
<<<<<<< HEAD
import com.unide.backend.domain.mypage.dto.UserStatsResponseDto;
=======
import com.unide.backend.domain.mypage.dto.UserStats;
>>>>>>> 5f34fd702810fe86351e09b810b61985a7b86361

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/UNIDE/rank")
public class MainpageController { 

    private final MainpageService mainpageService; 

    // 사용자 평판 순위
    @GetMapping("/user/reputation")
<<<<<<< HEAD
    public List<UserStatsResponseDto> getReputationRank(
=======
    public List<UserStats> getReputationRank(
>>>>>>> 5f34fd702810fe86351e09b810b61985a7b86361
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return mainpageService.getReputationRankList(size);
    }

    // 문제 평균 조회수 순위
   
@GetMapping("/problem/views")
public List<MainProblemViewRankDto> getProblemViewRank(
        @RequestParam(name = "date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate rankDate
) {
    if (rankDate == null) {
        rankDate = LocalDate.now();
    }
    return mainpageService.getProblemViewList(rankDate);
}
    // ✅ 코드 리뷰 순위 (vote_count 기준, 최근 7일, 기본 5개)
    @GetMapping("/reviews")
    public List<CodeReviewRankDto> getCodeReviewRank(
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        return mainpageService.getCodeReviewRankList(size);
    }
}
