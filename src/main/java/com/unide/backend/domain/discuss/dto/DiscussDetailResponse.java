package com.unide.backend.domain.discuss.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussDetailResponse {
    private Long postId;
    private String postTitle;
    private String contents;
    private String author;
    private String tag;
    private boolean anonymity;
    private int likeCount;
    private int commentCount;
    private boolean viewerLiked;      // 🔹 좋아요 API 응답에도 등장
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private List<String> attachments; // 첨부파일 URL 리스트
    // (필요하면 나중에 poll 정보도 여기에 추가)
}
