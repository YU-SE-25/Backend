package com.unide.backend.domain.discuss.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussListItemDto {
    private Long postId;
    private String postTitle;
    private String contents;
    private String author;
    private String tag;
    private boolean anonymity;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
}