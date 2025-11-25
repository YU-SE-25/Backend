package com.unide.backend.domain.qna.dto;

import java.time.LocalDateTime;

public class QnAPollResponse {

    private String message;
    private Long poll_id;
    private Long post_id;
    private LocalDateTime updated_at;

    public QnAPollResponse(String message, Long pollId, Long postId, LocalDateTime updatedAt) {
        this.message = message;
        this.poll_id = pollId;
        this.post_id = postId;
        this.updated_at = updatedAt;
    }

    public String getMessage() { return message; }
    public Long getPoll_id() { return poll_id; }
    public Long getPost_id() { return post_id; }
    public LocalDateTime getUpdated_at() { return updated_at; }
}
