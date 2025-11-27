package com.unide.backend.domain.studygroup.discuss.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class st_DiscussCommentRequest {

    // 댓글 내용
    @JsonProperty("contents")
    private String contents;

    // 익명 여부
    @JsonProperty("anonymity")
    private boolean anonymity;

    // 비공개 여부 (없으면 false 처리)
    @JsonProperty("is_private")
    private Boolean privatePost;

    // 대댓글이면 부모 댓글 ID
    @JsonProperty("parent_id")
    private Long parentId;
}
