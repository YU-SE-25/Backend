package com.unide.backend.domain.discuss.dto;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeResponse {
    private Long postId;
    private int likeCount;
    private boolean viewerLiked;
    private String message;
}