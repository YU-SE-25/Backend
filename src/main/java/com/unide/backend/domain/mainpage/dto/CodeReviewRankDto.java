// src/main/java/com/unide/backend/domain/mainpage/dto/CodeReviewRankDto.java
package com.unide.backend.domain.mainpage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CodeReviewRankDto {

    private final Long id;          // 리뷰 id
    private final Long authorId;    // 리뷰 작성자 id (reviewer_id)
    private final int rank;         // 현재 순위
    private final int delta;        // 전주 대비 순위 변화 (지금은 0으로 둘 거고, 나중에 확장 가능)
    private final int vote;         // vote_count
    // 필요하면 나중에 problemTitle / reviewTitle 필드 추가해서 확장하면 됨
}
