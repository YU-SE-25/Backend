// 공유된 풀이 목록 전체 응답 DTO (페이징 정보 포함)

package com.unide.backend.domain.submissions.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SubmissionSolutionListDto {
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private List<SubmissionSolutionDto> solutions;
}
