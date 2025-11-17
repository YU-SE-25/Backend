package com.unide.backend.domain.discuss.service;

import com.unide.backend.domain.discuss.dto.AttachRequest;
import com.unide.backend.domain.discuss.dto.AttachResponse;
import com.unide.backend.domain.discuss.entity.DisAttach;
import com.unide.backend.domain.discuss.entity.Discuss;
import com.unide.backend.domain.discuss.repository.DisAttachRepository;
import com.unide.backend.domain.discuss.repository.DiscussRepository;
import com.unide.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussAttachService {

    private final DiscussRepository discussRepository;
    private final DisAttachRepository disAttachRepository;

    public AttachResponse addAttachment(AttachRequest req, User user) {
        Discuss post = discussRepository.findById(req.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + req.getPostId()));

        // 정책에 따라 수정 가능 (작성자만 등록 가능하게 해둠)
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalStateException("작성자만 첨부파일을 등록할 수 있습니다.");
        }

        DisAttach attach = DisAttach.builder()
                .post(post)
                .contents(req.getContents())
                .build();

        disAttachRepository.save(attach);

        LocalDateTime now = LocalDateTime.now();
        return AttachResponse.builder()
                .message("첨부파일이 등록되었습니다.")
                .postId(post.getId())
                .updatedAt(now)
                .build();
    }
}
