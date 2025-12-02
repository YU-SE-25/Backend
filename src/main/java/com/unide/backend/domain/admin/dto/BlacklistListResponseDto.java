// 블랙리스트 목록 전체 응답 DTO (페이징 정보 포함)

package com.unide.backend.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BlacklistListResponseDto {
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private List<BlacklistSummaryDto> blacklist;
}
