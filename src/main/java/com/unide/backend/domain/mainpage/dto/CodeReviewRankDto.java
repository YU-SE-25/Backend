package com.unide.backend.domain.mainpage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeReviewRankDto {

    private Long reviewId;     // 코드 리뷰 id
    private Long authorId;     // 작성자 id (필요 없으면 나중에 제거 가능)
    private String nickname;   // ⭐ 작성자 닉네임

    private int ranking;       // ⭐ 현재 순위
    private int delta;         // ⭐ 순위 변화량 (이전 주 - 이번 주, 지금은 0)
    private int vote;          // 투표수 (score 개념)
}
