package com.unide.backend.domain.discuss.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.discuss.dto.DiscussPollCreateRequest;
import com.unide.backend.domain.discuss.dto.DiscussPollResponse;
import com.unide.backend.domain.discuss.dto.DiscussPollVoteRequest;
import com.unide.backend.domain.discuss.dto.DiscussPollVoteResponse;
import com.unide.backend.domain.discuss.entity.DiscussPoll;
import com.unide.backend.domain.discuss.entity.DiscussPollOption;
import com.unide.backend.domain.discuss.entity.DiscussPollVote;
import com.unide.backend.domain.discuss.repository.DiscussPollOptionRepository;
import com.unide.backend.domain.discuss.repository.DiscussPollRepository;
import com.unide.backend.domain.discuss.repository.DiscussPollVoteRepository;

@Service
@Transactional
public class DiscussPollService {

    private final DiscussPollRepository pollRepository;
    private final DiscussPollOptionRepository optionRepository;
    private final DiscussPollVoteRepository voteRepository;

    public DiscussPollService(
            DiscussPollRepository pollRepository,
            DiscussPollOptionRepository optionRepository,
            DiscussPollVoteRepository voteRepository
    ) {
        this.pollRepository = pollRepository;
        this.optionRepository = optionRepository;
        this.voteRepository = voteRepository;
    }


    // ===========================
    //  📌 투표 생성
    // ===========================
    public DiscussPollResponse createPoll(Long postId, Long authorId, DiscussPollCreateRequest request) {

        DiscussPoll poll = new DiscussPoll(
                postId,
                authorId,
                request.getTitle(),
                request.getEnd_time(),
                request.getIs_private() != null && request.getIs_private(),
                request.getAllows_multi() != null && request.getAllows_multi()
        );

        DiscussPoll savedPoll = pollRepository.save(poll);

        // 옵션 저장
        List<String> options = request.extractOptions();
        int idx = 1;

        for (String content : options) {
            if (content == null || content.isBlank()) continue;

            String label = String.valueOf(idx);
            DiscussPollOption option = new DiscussPollOption(savedPoll, label, content);
            optionRepository.save(option);

            idx++;
        }

        return new DiscussPollResponse(
                "투표가 등록되었습니다",
                savedPoll.getId(),
                savedPoll.getPostId(),
                savedPoll.getCreatedAt()
        );
    }


    // ===========================
    //  📌 투표하기
    // ===========================
    public DiscussPollVoteResponse vote(Long voterId, Long pollId, DiscussPollVoteRequest request) {

        DiscussPoll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다."));

        // 마감 시간 체크
        if (poll.getEndTime() != null && poll.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("이미 마감된 투표입니다.");
        }

        // 단일 선택이고 이미 투표한 경우
        if (!poll.isAllowsMulti() && voteRepository.existsByPollAndVoterId(poll, voterId)) {
            throw new IllegalStateException("이미 투표를 완료했습니다.");
        }

        // 옵션 조회
        DiscussPollOption option = optionRepository.findById(request.getOption_id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다."));

        // 옵션이 해당 poll 소속인지 확인
        if (!option.getPoll().getId().equals(poll.getId())) {
            throw new IllegalArgumentException("해당 투표의 옵션이 아닙니다.");
        }

        DiscussPollVote vote = new DiscussPollVote(poll, option, voterId);
        voteRepository.save(vote);

        return new DiscussPollVoteResponse("투표가 정상적으로 반영되었습니다.");
    }


    // ===========================
    //  📌 게시글(postId)로 투표 조회
    // ===========================
    @Transactional(readOnly = true)
    public DiscussPollResponse getPollByPostId(Long postId, Long userId) {

        DiscussPoll poll = pollRepository.findByPostId(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시글에는 투표가 없습니다. postId=" + postId)
                );

        DiscussPollResponse response = DiscussPollResponse.fromEntity(poll, userId);

        // 이미 투표했는지 여부
        if (userId != null) {
            boolean alreadyVoted = voteRepository.existsByPollAndVoterId(poll, userId);
            response.setAlreadyVoted(alreadyVoted);
        }

        return response;
    }
}
