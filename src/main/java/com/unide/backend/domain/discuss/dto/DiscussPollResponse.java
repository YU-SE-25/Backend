package com.unide.backend.domain.discuss.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.unide.backend.domain.discuss.entity.DiscussPoll;
import com.unide.backend.domain.discuss.entity.DiscussPollOption;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiscussPollResponse {

    private String message;
    private Long pollId;
    private Long postId;
    private String question;
    private List<DiscussPollOptionResponse> options;
    private int totalVotes;
    private boolean alreadyVoted;
    private LocalDateTime createdAt;

    // 생성 시 사용
    public DiscussPollResponse(String message, Long pollId, Long postId, LocalDateTime createdAt) {
        this.message = message;
        this.pollId = pollId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    // ✅ 조회용 변환 메서드
    public static DiscussPollResponse fromEntity(DiscussPoll poll, Long userId) {
        DiscussPollResponse dto = new DiscussPollResponse();

        dto.pollId = poll.getId();
        dto.postId = poll.getPostId();
        dto.question = poll.getTitle();        // 질문은 title 사용
        dto.createdAt = poll.getCreatedAt();

        List<DiscussPollOptionResponse> optionResponses = poll.getOptions().stream()
                .map(DiscussPollResponse::toOptionResponse)
                .collect(Collectors.toList());

        dto.options = optionResponses;

        dto.totalVotes = optionResponses.stream()
                .mapToInt(DiscussPollOptionResponse::getVoteCount)
                .sum();

        dto.alreadyVoted = false;

        return dto;
    }

    private static DiscussPollOptionResponse toOptionResponse(DiscussPollOption option) {
        return new DiscussPollOptionResponse(
                option.getId(),
                option.getLabel(),
                option.getContent(),
                option.getVoteCount()
        );
    }

    // ✅ 서비스에서 쓸 setter
    public void setAlreadyVoted(boolean alreadyVoted) {
        this.alreadyVoted = alreadyVoted;
    }
}
