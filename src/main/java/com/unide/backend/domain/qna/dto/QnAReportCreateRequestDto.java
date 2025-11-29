package com.unide.backend.domain.qna.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QnAReportCreateRequestDto {
    private String reason;   // 신고 사유만 받으면 됨
}
