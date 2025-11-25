package com.unide.backend.domain.qna.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unide.backend.domain.qna.entity.QnA;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnADto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long authorId;

    private Long postId;
    private boolean anonymous;
    private String title;
    private String contents;
    private boolean privatePost;

    // 👍 좋아요/댓글 수는 서버가 채움
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int likeCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int commentCount;

    // ← 문제 연동 정보
    private Long problemId;
    private QnAProblemDto problem;

    // 첨부파일 URL
    private String attachmentUrl;

    // 현재 사용자 기준 좋아요 여부
    @JsonProperty("viewerLiked")
    private boolean viewerLiked;

    // 좋아요 토글 응답에서만 쓰는 메시지 (하트 포함 💗)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String message;

    /* ===================== 정적 팩토리 메서드들 ===================== */

    /** 기본: problem, viewerLiked, message 신경 안 쓰는 경우 (목록, 단건 조회) */
    public static QnADto fromEntity(QnA qna) {
        return fromEntity(qna, null, false);
    }

    /** 문제 DTO만 같이 태우는 경우 (viewerLiked=false, message=null) */
    public static QnADto fromEntity(QnA qna, QnAProblemDto problemDto) {
        return fromEntity(qna, problemDto, false);
    }

    /** 풀옵션: QnA + 문제 + viewerLiked (message는 나중에 서비스에서 setMessage로 세팅) */
    public static QnADto fromEntity(QnA qna,
                                    QnAProblemDto problemDto,
                                    boolean viewerLiked) {

        if (qna == null) return null;

        QnADto dto = QnADto.builder()
                .postId(qna.getId())
                .authorId(qna.getAuthor() != null ? qna.getAuthor().getId() : null)
                .anonymous(qna.isAnonymous())
                .title(qna.getTitle())
                .contents(qna.getContents())
                .privatePost(qna.isPrivatePost())
                .likeCount(qna.getLikeCount())
                .commentCount(qna.getCommentCount())
                .attachmentUrl(qna.getAttachmentUrl())
                .viewerLiked(viewerLiked)
                .build();

        if (problemDto != null) {
            dto.setProblem(problemDto);
            dto.setProblemId(problemDto.getProblemId());
        }

        // message는 여기서 건드리지 않음 -> toggleLike에서만 채움
        return dto;
    }
}
