package com.unide.backend.domain.qna.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.qna.entity.QnA;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    // ← 문제 연동 정보
    private Long problemId;            // problem_post.problem_id
    private QnAProblemDto problem;     // 문제 상세 DTO (선택)

    private String attachmentUrl;

    /** QnA 엔티티만으로 만드는 기본 DTO */
    public static QnADto fromEntity(QnA qna) {
        return QnADto.builder()
                .postId(qna.getId())
                .authorId(qna.getAuthor() != null ? qna.getAuthor().getId() : null)
                .anonymous(qna.isAnonymous())
                .title(qna.getTitle())
                .contents(qna.getContents())
                .privatePost(qna.isPrivatePost())
                .likeCount(qna.getLikeCount())
                .commentCount(qna.getCommentCount())
                .attachmentUrl(qna.getAttachmentUrl())   
                .build();
    }

    /** QnA + 문제 DTO 까지 같이 태워주는 버전 */
    public static QnADto fromEntity(QnA qna, QnAProblemDto problemDto) {
        QnADto dto = fromEntity(qna);

        if (problemDto != null) {
            dto.setProblem(problemDto);
            dto.setProblemId(problemDto.getProblemId());
        }

        return dto;
    }
}
