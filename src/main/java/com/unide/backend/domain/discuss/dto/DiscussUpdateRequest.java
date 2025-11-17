package com.unide.backend.domain.discuss.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussUpdateRequest {
    private String postTitle;
    private String contents;
    private String tag;
    private boolean anonymity;
    private boolean isPrivate;
}