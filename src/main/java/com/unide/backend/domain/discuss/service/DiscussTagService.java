package com.unide.backend.domain.discuss.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.discuss.dto.DiscussDto;
import com.unide.backend.domain.discuss.dto.TagDto;
import com.unide.backend.domain.discuss.entity.Discuss;
import com.unide.backend.domain.discuss.entity.DiscussTag;
import com.unide.backend.domain.discuss.repository.DiscussLikeRepository;
import com.unide.backend.domain.discuss.repository.DiscussRepository;
import com.unide.backend.domain.discuss.repository.DiscussTagRepository;
import com.unide.backend.domain.discuss.repository.DistagRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;
import com.unide.backend.global.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussTagService {

    private final DiscussTagRepository discussTagRepository;
    private final DiscussRepository discussRepository;
    private final DiscussLikeRepository discussLikeRepository;
    private final UserRepository userRepository;
    private final DistagRepository distagRepository;   // ⭐ 태그 마스터 테이블(distag)용

    // ==========================
    // 1) 게시글에 태그 추가
    // ==========================
    public void addTagsToPost(Long postId, List<Long> tagIds) {
        // 기존 태그 삭제
        discussTagRepository.deleteByPostId(postId);

        // 새 태그 저장
        for (Long tagId : tagIds) {
            DiscussTag entity = new DiscussTag(postId, tagId);
            discussTagRepository.save(entity);
        }
    }

    // ==========================
    // 2) 특정 게시글의 태그 조회
    // ==========================
    public List<Long> getTagsByPost(Long postId) {
        return discussTagRepository.findByPostId(postId)
                .stream()
                .map(DiscussTag::getTagId)
                .toList();
    }

    // ==========================
    // 3) 전체 태그 목록 조회 (distag 기반)
    // ==========================
    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        return distagRepository.findAll().stream()
                .map(t -> new TagDto(
                        t.getTagId().longValue(),   // Integer → Long 변환
                        t.getTagName()
                ))
                .toList();
    }

    // ==========================
    // 4) 태그별 게시글 목록 조회
    // ==========================
    @Transactional(readOnly = true)
    public PageResponse<DiscussDto> getPostsByTag(Long tagId, int pageNum, Long viewerId) {

        // 1) 태그에 속한 postId 목록 가져오기
        List<Long> postIds = discussTagRepository.findByTagId(tagId)
                .stream()
                .map(DiscussTag::getPostId)
                .toList();

        // 태그에 연결된 게시글이 하나도 없으면 빈 페이지 반환
        if (postIds.isEmpty()) {
            return PageResponse.<DiscussDto>builder()
                    .content(List.of())
                    .page(pageNum)
                    .size(10)             // 페이지 사이즈
                    .totalElements(0L)
                    .totalPages(0)
                    .last(true)
                    .build();
        }

        PageRequest pageRequest = PageRequest.of(
                pageNum - 1,
                10,
                Sort.by(Sort.Direction.DESC, "postId")
        );

        // 2) postId 목록으로 게시글 조회
        Page<Discuss> page = discussRepository.findByPostIdIn(postIds, pageRequest);

        // 3) viewerLiked 계산 + authorName 넣기 → DiscussDto 생성
        List<DiscussDto> content = page.getContent().stream()
                .map(entity -> {
                    String authorName = resolveAuthorName(entity.getAuthorId());

                    boolean viewerLiked = false;
                    if (viewerId != null) {
                        viewerLiked = discussLikeRepository
                                .existsByIdPostIdAndIdLikerId(entity.getPostId(), viewerId);
                    }

                    return DiscussDto.fromEntity(entity, authorName, viewerLiked);
                })
                .toList();

        return PageResponse.<DiscussDto>builder()
                .content(content)
                .page(pageNum)
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    // ==========================
    // 내부 유틸: authorId → authorName
    // ==========================
    private String resolveAuthorName(Long authorId) {
        return userRepository.findById(authorId)
                .map(User::getNickname)
                .orElse("알 수 없음");
    }
}
