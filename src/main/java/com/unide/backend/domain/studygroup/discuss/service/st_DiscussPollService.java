package com.unide.backend.domain.studygroup.discuss.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussPollCreateRequest;
import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussPollResponse;
import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussPollVoteRequest;
import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussPollVoteResponse;
import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussPoll;
import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussPollOption;
import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussPollVote;
import com.unide.backend.domain.studygroup.discuss.repository.st_DiscussPollOptionRepository;
import com.unide.backend.domain.studygroup.discuss.repository.st_DiscussPollRepository;
import com.unide.backend.domain.studygroup.discuss.repository.st_DiscussPollVoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class st_DiscussPollService {

    private final st_DiscussPollRepository pollRepository;
    private final st_DiscussPollOptionRepository optionRepository;
    private final st_DiscussPollVoteRepository voteRepository;

    // ===========================
    // 📌 투표 생성
    // ===========================
    public st_DiscussPollResponse createPoll(Long groupId, Long postId, Long authorId,
                                             st_DiscussPollCreateRequest request) {

        st_DiscussPoll poll = st_DiscussPoll.builder()
                .postId(postId)
                .groupId(groupId)
                .authorId(authorId)
                .title(request.getTitle())
                .endTime(request.getEnd_time())
                .isPrivate(request.getIs_private() != null && request.getIs_private())
                .allowsMulti(request.getAllows_multi() != null && request.getAllows_multi())
                .build();

        st_DiscussPoll savedPoll = pollRepository.save(poll);

        // 옵션 저장
        List<String> options = request.extractOptions();
        int idx = 1;

        for (String content : options) {
            if (content == null || content.isBlank()) continue;

            String label = String.valueOf(idx);

            st_DiscussPollOption option = st_DiscussPollOption.builder()
                    .poll(savedPoll)
                    .label(label)
                    .content(content)
                    .build();

            optionRepository.save(option);
            idx++;
        }

        return new st_DiscussPollResponse(
                "투표가 생성되었습니다.",
                savedPoll.getPollId(),
                postId,
                savedPoll.getCreatedAt()
        );
    }

    // ===========================
    // 📌 투표하기
    // ===========================
    public st_DiscussPollVoteResponse vote(Long voterId,
                                           Long pollId,
                                           st_DiscussPollVoteRequest request) {

        st_DiscussPoll poll = pollRepository.findById(pollId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 투표가 없습니다. pollId=" + pollId));

        // 종료 여부
        if (poll.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("이미 종료된 투표입니다.");
        }

        // 이미 투표했는지 검사
boolean alreadyVoted = voteRepository.existsByPoll_PollIdAndVoterId(pollId, voterId);
        if (alreadyVoted) {
            throw new IllegalStateException("이미 투표한 사용자입니다.");
        }

        // 옵션 조회
        List<st_DiscussPollOption> selectedOptions =
                optionRepository.findAllById(request.getOptionIds());

        if (selectedOptions.isEmpty()) {
            throw new IllegalArgumentException("선택된 옵션이 없습니다.");
        }

        // 다중 선택 불가 검사
        // 다중 선택 불가 검사
    if (!poll.isAllowsMulti() && selectedOptions.size() > 1) {
    throw new IllegalArgumentException("이 투표는 다중 선택이 불가능합니다.");
    }


        // 저장 + 옵션 카운트 증가
        for (st_DiscussPollOption option : selectedOptions) {
            st_DiscussPollVote vote = st_DiscussPollVote.builder()
                    .poll(poll)
                    .option(option)
                    .voterId(voterId)
                    .votedAt(LocalDateTime.now())
                    .build();

            voteRepository.save(vote);

            option.setVoteCount(option.getVoteCount() + 1);
            optionRepository.save(option);
        }

        return new st_DiscussPollVoteResponse(
                "투표가 완료되었습니다.",
                pollId,
                voterId,
                LocalDateTime.now()
        );
    }

    // ===========================
    // 📌 게시글 기반 투표 조회
    // ===========================
    public st_DiscussPollResponse getPollByPostId(Long postId, Long userId) {

        st_DiscussPoll poll = pollRepository.findByPostId(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시글에 투표가 없습니다. postId=" + postId));

        // 기본 응답
        st_DiscussPollResponse response =
                st_DiscussPollResponse.fromEntity(poll, userId);

        // 이미 투표했는지 표시
        if (userId != null) {
boolean alreadyVoted = voteRepository.existsByPoll_PollIdAndVoterId(poll.getPollId(), userId);
            response.setAlreadyVoted(alreadyVoted);
        }

        return response;
    }
}
