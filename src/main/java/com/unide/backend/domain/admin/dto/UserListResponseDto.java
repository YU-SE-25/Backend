// 전체 사용자 목록 응답 DTO (페이징 정보 포함)

package com.unide.backend.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserListResponseDto {
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private List<UserSummaryDto> users;
}
