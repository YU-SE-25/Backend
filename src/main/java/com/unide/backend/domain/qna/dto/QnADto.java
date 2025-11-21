package com.unide.backend.domain.qna.dto;

import com.unide.backend.domain.qna.entity.QnA;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QnADto {

    private Long postId;
    private Long authorId;
    private boolean anonymous;
    private String title;
    private String contents;
    private boolean privatePost;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;
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
