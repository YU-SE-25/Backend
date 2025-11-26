package com.unide.backend.domain.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QnAPollOptionResponse {

    private Long optionId;
    private String label;      // "1", "2", "3" 같은 번호
    private String content;    // 보기 내용
    private int voteCount;     // 이 옵션에 찍힌 표 수
}
