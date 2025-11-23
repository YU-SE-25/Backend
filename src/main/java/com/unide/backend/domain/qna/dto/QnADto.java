package com.unide.backend.domain.qna.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.qna.entity.QnA;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QnADto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)   // 🔒 요청에서 들어오는 값은 무시
    private Long authorId;

    private Long postId;
    private boolean anonymous;
    private String title;
    private String contents;
    private boolean privatePost;
     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int likeCount;
     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int commentCount;
     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;


    // QnA 엔티티 → DTO 변환
    public static QnADto fromEntity(QnA qna) {
        if (qna == null) return null;

        return QnADto.builder()
                .postId(qna.getPostId())
                .authorId(qna.getAuthorId())
                .anonymous(qna.isAnonymous())
                .title(qna.getTitle())
                .contents(qna.getContents())
                .privatePost(qna.isPrivatePost())
                .likeCount(qna.getLikeCount())
                .commentCount(qna.getCommentCount())
                .createdAt(qna.getCreatedAt())
                .updatedAt(qna.getUpdatedAt())
                .build();
    }
}
