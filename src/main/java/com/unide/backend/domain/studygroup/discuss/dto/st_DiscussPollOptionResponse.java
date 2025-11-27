package com.unide.backend.domain.studygroup.discuss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class st_DiscussPollOptionResponse {

    private Long optionId;
    private String label;
    private String content;
    private int voteCount;
}
