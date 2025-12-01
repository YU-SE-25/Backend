package com.unide.backend.domain.efficiency.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.efficiency.dto.SubmissionEfficiencyDto;
import com.unide.backend.domain.review.repository.CodeReviewVoteRepository;
import com.unide.backend.domain.submissions.entity.Submissions;
import com.unide.backend.domain.submissions.repository.SubmissionsRepository;
import com.unide.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EfficiencyService {

    private final SubmissionsRepository submissionsRepository;
    private final CodeReviewVoteRepository codeReviewVoteRepository;

    /**
     * 특정 문제(problemId)에 대해 "공유된" 제출물들의 효율 랭킹 계산.
     * 기준: 해당 제출물의 모든 리뷰에 달린 vote 수 합계.
     */
    public List<SubmissionEfficiencyDto> calculateEfficiencyForProblem(Long problemId) {

        // 1) 이 문제에 대한 공유된 제출물 전체 조회
        List<Submissions> submissions =
                submissionsRepository.findByProblemIdAndIsSharedTrue(problemId);

        if (submissions.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> submissionIds = submissions.stream()
                .map(Submissions::getId)
                .collect(Collectors.toList());

        // 2) code_review_vote 기준으로 제출물별 vote 합계 조회
        List<Object[]> rows =
                codeReviewVoteRepository.countVotesBySubmissionIds(submissionIds);

        Map<Long, Integer> voteMap = new HashMap<>();
        for (Object[] row : rows) {
            Long submissionId = (Long) row[0];
            Long voteCount    = (Long) row[1];
            voteMap.put(submissionId, voteCount.intValue());
        }

        // 3) DTO 리스트 구성
        List<SubmissionEfficiencyDto> list = new ArrayList<>();

        for (Submissions s : submissions) {
            Long submissionId = s.getId();
            User author = s.getUser();   // Submissions 엔티티에 user 필드 있다고 가정

            int totalVotes = voteMap.getOrDefault(submissionId, 0);
            double score = totalVotes;   // 지금은 그냥 좋아요 수가 점수

            list.add(SubmissionEfficiencyDto.builder()
                    .submissionId(submissionId)
                    .authorId(author != null ? author.getId() : null)
                    .authorName(author != null ? author.getNickname() : "Unknown")
                    .totalVotes(totalVotes)
                    .efficiencyScore(score)
                    .build());
        }

        // 4) 점수 기준 내림차순 + rank 부여
        list.sort((a, b) -> Double.compare(b.getEfficiencyScore(), a.getEfficiencyScore()));

        long rank = 1;
        for (SubmissionEfficiencyDto dto : list) {
            dto.setRank(rank++);
        }

        return list;
    }

    /**
     * 특정 사용자(userId)의 풀이가 이 문제에서 몇 등인지 알고 싶을 때.
     */
    public Long getRankForUser(Long problemId, Long userId) {
        return calculateEfficiencyForProblem(problemId).stream()
                .filter(dto -> Objects.equals(dto.getAuthorId(), userId))
                .map(SubmissionEfficiencyDto::getRank)
                .findFirst()
                .orElse(null);
    }
}
