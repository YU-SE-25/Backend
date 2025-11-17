package com.unide.backend.domain.discuss.service;

import com.unide.backend.domain.discuss.dto.*;
import com.unide.backend.domain.discuss.entity.DisAttach;
import com.unide.backend.domain.discuss.entity.Discuss;
import com.unide.backend.domain.discuss.repository.DisAttachRepository;
import com.unide.backend.domain.discuss.repository.DiscussRepository;
import com.unide.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussService {

    private final DiscussRepository discussRepository;
    private final DisAttachRepository disAttachRepository;

    // ◆ 게시글 목록 조회
    @Transactional(readOnly = true)
    public DiscussListResponse getDiscussList(int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(
                pageNumber - 1,
                pageSize,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Discuss> page = discussRepository.findAll(pageable);

        List<DiscussListItemDto> posts = page.getContent().stream()
                .map(post -> {
                    DiscussListItemDto dto = new DiscussListItemDto();
                    dto.setPostId(post.getId());
                    dto.setPostTitle(post.getTitle());
                    dto.setContents(post.getContents());
                    dto.setAuthor(
                            post.isAnonymous()
                                    ? "익명"
                                    : String.valueOf(post.getAuthor().getId())
                    );
                    dto.setTag(post.getTag());
                    dto.setAnonymity(post.isAnonymous());
                    dto.setLikeCount(post.getLikeCount());
                    dto.setCommentCount(post.getCommentCount());
                    // createdAt / modifiedAt 은 BaseTimeEntity 이름을 몰라서 일단 생략
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());

        return DiscussListResponse.builder()
                .page(pageNumber)
                .pageSize(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .posts(posts)
                .build();
    }

    // ◆ 게시글 상세 조회
    @Transactional(readOnly = true)
    public DiscussDetailResponse getDiscussDetail(Long postId, User viewer) {

        Discuss post = discussRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));

        boolean viewerLiked = false; // 좋아요 여부는 LikeService에서 처리 예정

        List<String> attachments = disAttachRepository.findByPost(post).stream()
                .map(DisAttach::getContents)
                .toList();

        return DiscussDetailResponse.builder()
                .postId(post.getId())
                .postTitle(post.getTitle())
                .contents(post.getContents())
                .author(post.isAnonymous() ? "익명" : String.valueOf(post.getAuthor().getId()))
                .tag(post.getTag())
                .anonymity(post.isAnonymous())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .viewerLiked(viewerLiked)
                // BaseTimeEntity 필드 이름을 몰라서 createTime/modifyTime은 일단 null 로 둔다
                .attachments(attachments)
                .build();
    }

    // ◆ 게시글 생성
    public DiscussCreateResponse createDiscuss(User author, DiscussCreateRequest req) {

        Discuss post = Discuss.builder()
                .author(author)
                // 🔥 Lombok Builder 는 필드 이름 기준으로 메서드가 생성됨
                // 필드명이 isAnonymous 이므로 builder 메서드는 isAnonymous(...)
                .isAnonymous(req.isAnonymity())
                .title(req.getPostTitle())
                .contents(req.getContents())
                .tag(req.getTag())
                .isPrivate(req.isPrivate())
                .likeCount(0)
                .commentCount(0)
                .build();

        Discuss saved = discussRepository.save(post);

        return DiscussCreateResponse.builder()
                .message("게시글이 등록되었습니다.")
                .postId(saved.getId())
                .location("/api/dis_board/" + saved.getId())
                .build();
    }

    // ◆ 게시글 수정
    public DiscussUpdateResponse updateDiscuss(Long postId, User editor, DiscussUpdateRequest req) {

        Discuss post = discussRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));

        if (!post.getAuthor().getId().equals(editor.getId())) {
            throw new IllegalStateException("작성자만 게시글을 수정할 수 있습니다.");
            }

        post.update(
                req.getPostTitle(),
                req.getContents(),
                req.getTag(),
                req.isPrivate(),
                req.isAnonymity()
        );

        return DiscussUpdateResponse.builder()
                .message("게시글이 수정되었습니다.")
                .postId(post.getId())
                // 수정시간도 BaseTimeEntity 이름을 몰라서 일단 null
                .build();
    }

    // ◆ 게시글 삭제
    public DiscussDeleteResponse deleteDiscuss(Long postId, User requester) {

        Discuss post = discussRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));

        if (!post.getAuthor().getId().equals(requester.getId())) {
            throw new IllegalStateException("작성자만 게시글을 삭제할 수 있습니다.");
        }

        discussRepository.delete(post);

        return DiscussDeleteResponse.builder()
                .message("게시글이 삭제되었습니다.")
                .deletedId(postId)
                .redirect("/api/dis_board_list/1")
                .build();
    }
}
