package com.unide.backend.domain.discuss.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollCreateRequest {
    private Long postId;
    private String option1;
    private String option2;
    private String option3;    // null 가능
}