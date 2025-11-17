package com.unide.backend.domain.discuss.service;

import com.unide.backend.domain.discuss.dto.PollCreateRequest;
import com.unide.backend.domain.discuss.dto.PollCreateResponse;
import com.unide.backend.domain.discuss.entity.DisPoll;
import com.unide.backend.domain.discuss.entity.Discuss;
import com.unide.backend.domain.discuss.repository.DisPollRepository;
import com.unide.backend.domain.discuss.repository.DiscussRepository;
import com.unide.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussPollService {

    private final DiscussRepository discussRepository;
    private final DisPollRepository disPollRepository;

    public PollCreateResponse createPoll(PollCreateRequest req, User user) {
        Discuss post = discussRepository.findById(req.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + req.getPostId()));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalStateException("작성자만 투표를 생성할 수 있습니다.");
        }

        if (disPollRepository.existsByPost(post)) {
            throw new IllegalStateException("이미 투표가 존재하는 게시글입니다.");
        }

        DisPoll poll = DisPoll.builder()
                .post(post)
                .option1(req.getOption1())
                .option2(req.getOption2())
                .option3(req.getOption3())
                .build();

        disPollRepository.save(poll);

        return PollCreateResponse.builder()
                .message("투표가 등록되었습니다.")
                .build();
    }
}
