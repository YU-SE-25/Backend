package com.unide.backend.domain.discuss.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussCreateRequest {
    // 🔹 API 요청 JSON: post_title, contents, tag, anonymity, is_private
    private String postTitle;
    private String contents;
    private String tag;
    private boolean anonymity;
    private boolean isPrivate;
}
