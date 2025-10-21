// 강사 지원 목록 전체 응답을 담는 DTO (페이지 정보 포함)

package com.unide.backend.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class InstructorApplicationListResponseDto {
    private long totalCount; // 전체 지원 건수
    private int currentPage; // 현재 페이지 번호
    private int pageSize; // 페이지 당 항목 수
    private List<InstructorApplicationSummaryDto> applications; // 현재 페이지 지원 목록
}
