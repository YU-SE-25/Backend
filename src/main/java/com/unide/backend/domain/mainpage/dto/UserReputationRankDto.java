package com.unide.backend.domain.mainpage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserReputationRankDto {

    private final Long userId;
    private final int rank;
    private final int delta;
}
