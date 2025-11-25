package com.unide.backend.domain.qna.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.unide.backend.domain.qna.dto.QnAPollCreateRequest;
import com.unide.backend.domain.qna.dto.QnAPollResponse;
import com.unide.backend.domain.qna.dto.QnAPollVoteRequest;
import com.unide.backend.domain.qna.dto.QnAPollVoteResponse;
import com.unide.backend.domain.qna.entity.QnAPoll;
import com.unide.backend.domain.qna.entity.QnAPollOption;
import com.unide.backend.domain.qna.entity.QnAPollVote;
import com.unide.backend.domain.qna.repository.QnAPollOptionRepository;
import com.unide.backend.domain.qna.repository.QnAPollRepository;
import com.unide.backend.domain.qna.repository.QnAPollVoteRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class QnAPollService {

    private final QnAPollRepository pollRepository;
    private final QnAPollOptionRepository optionRepository;
    private final QnAPollVoteRepository voteRepository;

    public QnAPollService(QnAPollRepository pollRepository,
                          QnAPollOptionRepository optionRepository,
                          QnAPollVoteRepository voteRepository) {
        this.pollRepository = pollRepository;
        this.optionRepository = optionRepository;
        this.voteRepository = voteRepository;
    }

    // 투표 생성
   // QnAPollService.java 안

@Transactional
public QnAPollResponse createPoll(Long postId, Long authorId, QnAPollCreateRequest request) {

    // 1) Poll 엔티티 생성
    QnAPoll poll = new QnAPoll(
            postId,
            authorId,
            request.getTitle(),
            request.getEnd_time(),
            request.getIs_private() != null && request.getIs_private(),
            request.getAllows_multi() != null && request.getAllows_multi()
    );

    QnAPoll savedPoll = pollRepository.save(poll);

    // 2) 옵션 저장
    List<String> options = request.extractOptions();  // option1, option2, ... null 제외해서 리턴한다고 가정
    int idx = 1;                                      // 🔥 label용 인덱스

    for (String content : options) {
        if (content == null || content.isBlank()) {
            continue;
        }

        String label = String.valueOf(idx);          // "1", "2", "3" ...
        QnAPollOption option = new QnAPollOption(savedPoll, label, content);
        optionRepository.save(option);

        idx++;
    }

    // 3) 응답 DTO
    return new QnAPollResponse(
            "투표가 등록되었습니다",
            savedPoll.getId(),
            savedPoll.getPostId(),
            savedPoll.getCreatedAt()
    );
}


    // 투표 하기
    public QnAPollVoteResponse vote(Long voterId, Long pollId, QnAPollVoteRequest request) {

        QnAPoll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다."));

        // 마감 시간 체크
        if (poll.getEndTime() != null && poll.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("이미 마감된 투표입니다.");
        }

        // 단일 선택 투표면, 이미 한 번이라도 투표했는지 체크
        if (!poll.isAllowsMulti() && voteRepository.existsByPollAndVoterId(poll, voterId)) {
            throw new IllegalStateException("이미 투표를 완료했습니다.");
        }

        QnAPollOption option = optionRepository.findById(request.getOption_id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다."));

        // 옵션이 해당 poll에 속하는지 검증
        if (!option.getPoll().getId().equals(poll.getId())) {
            throw new IllegalArgumentException("해당 투표의 옵션이 아닙니다.");
        }

        QnAPollVote vote = new QnAPollVote(poll, option, voterId);
        voteRepository.save(vote);

        return new QnAPollVoteResponse("투표가 정상적으로 반영되었습니다.");
    }
    
}
