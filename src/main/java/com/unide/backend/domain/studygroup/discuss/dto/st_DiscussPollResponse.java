package com.unide.backend.domain.studygroup.discuss.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussPoll;
import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussPollOption;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class st_DiscussPollResponse {

    private String message;
    private Long pollId;
    private Long postId;
    private String question;
    private List<st_DiscussPollOptionResponse> options;
    private int totalVotes;
    private boolean alreadyVoted;
    private LocalDateTime createdAt;

    // 투표 생성 직후 응답용
    public st_DiscussPollResponse(String message, Long pollId, Long postId, LocalDateTime createdAt) {
        this.message = message;
        this.pollId = pollId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    // ✅ 조회용 변환 메서드
    public static st_DiscussPollResponse fromEntity(st_DiscussPoll poll, Long userId) {
        st_DiscussPollResponse dto = new st_DiscussPollResponse();

        dto.pollId = poll.getPollId();
        dto.postId = poll.getPostId();
        dto.question = poll.getTitle();
        dto.createdAt = poll.getCreatedAt();

        // 옵션 리스트 → DTO 리스트
        List<st_DiscussPollOptionResponse> optionResponses = poll.getOptions().stream()
                .map(st_DiscussPollResponse::toOptionResponse)
                .collect(Collectors.toList());

        dto.options = optionResponses;

        // 총 투표 수
        dto.totalVotes = optionResponses.stream()
                .mapToInt(o -> o.getVoteCount())      // ← 메서드 레퍼런스 대신 람다로
                .sum();

        dto.alreadyVoted = false;
        return dto;
    }

    private static st_DiscussPollOptionResponse toOptionResponse(st_DiscussPollOption option) {
        return new st_DiscussPollOptionResponse(
                option.getOptionId(),
                option.getLabel(),
                option.getContent(),
                option.getVoteCount()
        );
    }

    // 서비스에서 이미 투표 여부 세팅할 때 사용
    public void setAlreadyVoted(boolean alreadyVoted) {
        this.alreadyVoted = alreadyVoted;
    }
}
