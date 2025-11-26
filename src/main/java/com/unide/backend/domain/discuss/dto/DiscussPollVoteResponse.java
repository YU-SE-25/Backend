package com.unide.backend.domain.discuss.dto;

public class DiscussPollVoteResponse {

    private String message;

    public DiscussPollVoteResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}
