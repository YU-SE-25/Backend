package com.unide.backend.domain.studygroup.discuss.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class st_DiscussPollVoteResponse {

    private String message;
    private Long pollId;
    private Long voterId;
    private LocalDateTime votedAt;
}
