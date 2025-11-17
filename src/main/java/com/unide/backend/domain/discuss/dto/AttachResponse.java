package com.unide.backend.domain.discuss.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachResponse {
    private String message;
    private Long postId;
    private LocalDateTime updatedAt;
}