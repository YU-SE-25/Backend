package com.unide.backend.global.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;     // 현재 페이지 데이터
    private int page;            // 0-based or 1-based (우리가 쓴 기준)
    private int size;            // 페이지 사이즈
    private long totalElements;  // 전체 글 개수
    private int totalPages;      // 전체 페이지 수
    private boolean last;        // 마지막 페이지 여부
}
