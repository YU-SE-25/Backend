package com.unide.backend.domain.mainpage.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate; 
// --- 추가된 부분 ---
import com.unide.backend.domain.mainpage.service.MainpageService;
// -----------------

// 현재 사용하지 않는 DTO는 주석 처리합니다.
// import com.unide.backend.domain.mainpage.dto.ReputationRankDto; 
// import com.unide.backend.domain.mainpage.dto.CodeReviewRankDto; 
import com.unide.backend.domain.mainpage.dto.MainProblemViewRankDto; 

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/UNIDE/rank")
public class MainpageController { 
    private final MainpageService mainpageService; 

    // // 사용자 평판 순위 (미구현)
    // @GetMapping ("/user/reputation")
    // public List<ReputationRankDto> getReputationRank(
    //     @RequestParam("startTime") LocalDateTime rankStartTime) {
        
    //     // return mainpageService.getReputationRankList(rankStartTime);
    //     return null;
    // }

    //문제 평균 조회수 순위
    @GetMapping("/problem/views")
    public List<MainProblemViewRankDto> getProblemViewRank( 
        @RequestParam("date") LocalDate rankDate) {
        
        // 문제 조회수 순위 기능에 집중합니다.
        return mainpageService.getProblemViewList(rankDate); 
    }

    // // 코드 리뷰 순위 (미구현)
    // @GetMapping("/reviews")
    // public List<CodeReviewRankDto> getCodeReviewRank(
    //     @RequestParam("startTime") LocalDateTime reviewStartTime) {
        
    //     // return mainpageService.getCodeReviewRankList(reviewStartTime);
    //     return null;
    // }
}