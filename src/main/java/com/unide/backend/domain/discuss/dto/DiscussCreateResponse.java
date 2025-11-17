package com.unide.backend.domain.discuss.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussCreateResponse {
    private String message;
    private Long postId;
    private String location;      // "/api/dis_board/{postId}"
}

