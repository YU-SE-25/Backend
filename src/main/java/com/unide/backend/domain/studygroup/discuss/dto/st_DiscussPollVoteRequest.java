package com.unide.backend.domain.studygroup.discuss.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class st_DiscussPollVoteRequest {
    // 사용자가 선택한 option_id 여러 개
    private List<Long> optionIds;
}
