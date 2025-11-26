package com.unide.backend.domain.qna.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


import com.unide.backend.domain.qna.entity.QnAPoll;
import com.unide.backend.domain.qna.entity.QnAPollOption;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QnAPollResponse {

    // === 공통 필드 ===
    private String message;   // 생성 시 메시지 용 (선택)
    private Long pollId;
    private Long postId;
    private String question;
    private List<QnAPollOptionResponse> options;
    private int totalVotes;
    private boolean alreadyVoted;
    private LocalDateTime createdAt;

    // === 투표 생성용 생성자 (이미 서비스에서 쓰고 있는 그거) ===
    public QnAPollResponse(String message, Long pollId, Long postId, LocalDateTime createdAt) {
        this.message = message;
        this.pollId = pollId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    // === 조회용 변환 메서드 ===
    public static QnAPollResponse fromEntity(QnAPoll poll, Long userId) {
    QnAPollResponse dto = new QnAPollResponse();

    dto.pollId = poll.getId();
    dto.postId = poll.getPostId();
    dto.question = poll.getQuestion();
    dto.createdAt = poll.getCreatedAt();

    // ✅ poll.getOptions() → List<QnAPollOption>
    List<QnAPollOptionResponse> optionResponses = poll.getOptions().stream()
            .map(QnAPollResponse::toOptionResponse)
            .collect(Collectors.toList());    // 또는 .toList() (Java 17이면 OK)

    dto.options = optionResponses;

    dto.totalVotes = optionResponses.stream()
            .mapToInt(QnAPollOptionResponse::getVoteCount)
            .sum();

    dto.alreadyVoted = false; // 기본값

    return dto;
}
    private static QnAPollOptionResponse toOptionResponse(QnAPollOption option) {
    return new QnAPollOptionResponse(
            option.getId(),
            option.getLabel(),
            option.getContent(),
            option.getVoteCount()
    );
}

    // Service에서 사용하기 위한 setter 하나만 허용
    public void setAlreadyVoted(boolean alreadyVoted) {
        this.alreadyVoted = alreadyVoted;
    }
}
