// 내 제출 이력 전체 응답 DTO (페이징 정보 포함)

package com.unide.backend.domain.submissions.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SubmissionHistoryListDto {
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private List<SubmissionHistoryDto> submissions;
}
