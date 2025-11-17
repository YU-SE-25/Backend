package com.unide.backend.domain.discuss.service;

import com.unide.backend.domain.discuss.dto.PostLikeResponse;
import com.unide.backend.domain.discuss.entity.DisPostLike;
import com.unide.backend.domain.discuss.entity.DisPostLikeId;
import com.unide.backend.domain.discuss.entity.Discuss;
import com.unide.backend.domain.discuss.repository.DisPostLikeRepository;
import com.unide.backend.domain.discuss.repository.DiscussRepository;
import com.unide.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussLikeService {

    private final DiscussRepository discussRepository;
    private final DisPostLikeRepository disPostLikeRepository;

    public PostLikeResponse toggleLike(Long postId, User liker) {
        Discuss post = discussRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));

        boolean alreadyLiked = disPostLikeRepository.existsByPostAndLiker(post, liker);
        String message;

        if (alreadyLiked) {
            disPostLikeRepository.deleteByPostAndLiker(post, liker);
            post.getLikeCount();
            message = "좋아요가 취소되었습니다.";
        } else {
            DisPostLike like = DisPostLike.builder()
                    .id(new DisPostLikeId(post.getId(), liker.getId()))
                    .post(post)
                    .liker(liker)
                    .build();
            disPostLikeRepository.save(like);
            post.getLikeCount();
            message = "좋아요가 추가되었습니다.";
        }

        return PostLikeResponse.builder()
                .postId(post.getId())
                .likeCount(post.getLikeCount())
                .viewerLiked(!alreadyLiked)
                .message(message)
                .build();
    }
}
