package com.unide.backend.domain.qna.dto;

public class QnAPollVoteResponse {

    private String message;

    public QnAPollVoteResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}
