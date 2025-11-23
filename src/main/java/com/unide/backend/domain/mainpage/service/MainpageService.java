package com.unide.backend.domain.mainpage.service;

import com.unide.backend.domain.mainpage.dto.MainProblemViewRankDto; 
import com.unide.backend.domain.mainpage.repository.MainProblemViewRankRepository; // Repository import 변경
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainpageService { 
    
    // Repository 타입과 필드 이름 변경
    private final MainProblemViewRankRepository mainProblemViewRankRepository;
    
    // ... 사용자 평판 순위 및 코드 리뷰 순위 메서드는 아직 미구현 상태입니다. ...

    /**
     * 주간 문제 조회수 순위 목록을 조회하고, 이전 주 대비 순위 변동(delta)을 계산합니다.
     * @param rankDate 현재 주간 순위의 기준 날짜
     * @return 순위 및 delta가 포함된 문제 목록 DTO
     */
    public List<MainProblemViewRankDto> getProblemViewList(LocalDate rankDate) {
        // 1. 현재 주간 순위 목록 조회
        List<MainProblemViewRankDto> currentBaseList = 
            mainProblemViewRankRepository.findRankedProblemsByDate(rankDate); // 변경된 필드 이름 사용

        // 2. 이전 주간 순위 조회 기준일 계산
        LocalDate previousDate = rankDate.minusWeeks(1); 
        
        // 3. 이전 주간 순위 목록 조회
        List<MainProblemViewRankDto> previousBaseList = 
            mainProblemViewRankRepository.findRankedProblemsByDate(previousDate); // 변경된 필드 이름 사용
        
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
            } else {
                delta = 0; 
            }
            
            // 최종 DTO 생성 및 목록에 추가
            finalRankList.add(new MainProblemViewRankDto(currentDto, currentRank, delta));
        }

        return finalRankList;
    }

    /**
     * 조회수 정렬된 목록을 입력받아, 문제 ID를 키로 하고 순위(1-based)를 값으로 하는 Map을 반환합니다.
     */
    private Map<Long, Integer> createProblemRankMap(List<MainProblemViewRankDto> rankedList) {
        Map<Long, Integer> rankMap = new HashMap<>();
        for (int i = 0; i < rankedList.size(); i++) {
            rankMap.put(rankedList.get(i).getProblemId(), i + 1); 
        }
        return rankMap;
    }
}