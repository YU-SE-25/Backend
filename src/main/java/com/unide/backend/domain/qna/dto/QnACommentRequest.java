package com.unide.backend.domain.qna.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnACommentRequest {
    
    // 댓글 내용
    @JsonProperty("contents")
    private String contents;

    // 익명 여부 (문서에서 anonymity 라고 써 있어서 여기에 매핑)
    @JsonProperty("anonymity")
    private boolean anonymity;

    // 비공개 여부 (없으면 false 처리)
    @JsonProperty("is_private")
    private Boolean privatePost;

    // 대댓글일 때 부모 댓글 ID (최상위 댓글이면 null)
    @JsonProperty("parent_id")
    private Long parentId;

}
