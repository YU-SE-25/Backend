package com.unide.backend.domain.discuss.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.discuss.dto.DiscussDto;
import com.unide.backend.domain.discuss.entity.Discuss;
import com.unide.backend.domain.discuss.entity.DiscussLike;
import com.unide.backend.domain.discuss.repository.DiscussLikeRepository;
import com.unide.backend.domain.discuss.repository.DiscussRepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussService {

    private final DiscussRepository discussRepository;
    private final DiscussLikeRepository discussLikeRepository;

    // 🔹 authorId -> authorName 조회용
    private final UserRepository userRepository;

    // ==================== 공통 유틸 ====================

    /**
     * authorId 로부터 작성자 이름(닉네임)을 조회합니다.
     *  - User 엔티티의 필드명이 name 이면 getName() 으로 변경하세요.
     */
    private String resolveAuthorName(Long authorId) {
        if (authorId == null) return "알 수 없음";

        return userRepository.findById(authorId)
                .map(User::getNickname)   // 🔹 getName() / getNickname() 등 프로젝트에 맞게 수정
                .orElse("알 수 없음");
    }

    // ==================== 목록 조회 ====================

    @Transactional(readOnly = true)
    public List<DiscussDto> getDiscussList(int pageNum) {
        PageRequest pageRequest = PageRequest.of(
                pageNum - 1,
                10,
                Sort.by(Sort.Direction.DESC, "postId")
        );

        Page<Discuss> page = discussRepository.findAll(pageRequest);

        return page.stream()
                .map(entity -> {
                    String authorName = resolveAuthorName(entity.getAuthorId());
                    return DiscussDto.fromEntity(entity, authorName);
                })
                .collect(Collectors.toList());
    }

    // ==================== 단건 조회 ====================

    @Transactional(readOnly = true)
    public DiscussDto getDiscuss(Long postId) {
        Discuss discuss = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 토론글이 없습니다. postId=" + postId));

        String authorName = resolveAuthorName(discuss.getAuthorId());
        return DiscussDto.fromEntity(discuss, authorName);
    }

    // ==================== 생성 ====================

    public DiscussDto createDiscuss(DiscussDto dto, Long authorId) {
        Discuss discuss = Discuss.builder()
                .authorId(authorId)
                .anonymous(dto.isAnonymous())
                .title(dto.getTitle())
                .contents(dto.getContents())
                .privatePost(dto.isPrivatePost())
                .likeCount(0)
                .commentCount(0)
                .build();

        Discuss saved = discussRepository.save(discuss);

        String authorName = resolveAuthorName(saved.getAuthorId());
        return DiscussDto.fromEntity(saved, authorName);
    }

    // ==================== 수정 ====================

    public DiscussDto updateDiscuss(Long postId, DiscussDto dto) {
        Discuss discuss = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 토론글이 없습니다. postId=" + postId));

        discuss.setTitle(dto.getTitle());
        discuss.setContents(dto.getContents());
        discuss.setAnonymous(dto.isAnonymous());
        discuss.setPrivatePost(dto.isPrivatePost());

        String authorName = resolveAuthorName(discuss.getAuthorId());
        return DiscussDto.fromEntity(discuss, authorName);
    }

    // ==================== 삭제 ====================

    public void deleteDiscuss(Long postId) {
        if (!discussRepository.existsById(postId)) {
            throw new IllegalArgumentException("해당 토론글이 없습니다. postId=" + postId);
        }
        discussRepository.deleteById(postId);
    }

    // ==================== 검색 ====================

    @Transactional(readOnly = true)
    public List<DiscussDto> searchDiscusses(String keyword) {
        List<Discuss> list = discussRepository
                .findByTitleContainingIgnoreCaseOrContentsContainingIgnoreCase(keyword, keyword);

        return list.stream()
                .map(entity -> {
                    String authorName = resolveAuthorName(entity.getAuthorId());
                    return DiscussDto.fromEntity(entity, authorName);
                })
                .collect(Collectors.toList());
    }

    // ==================== 첨부파일 추가 ====================

    @Transactional
    public Map<String, Object> attachFile(Long postId, String fileUrl) {

        Discuss post = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시물이 없습니다. postId=" + postId));

        post.setAttachmentUrl(fileUrl);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "첨부파일이 등록되었습니다.");
        response.put("post_id", postId);
        response.put("updated_at", LocalDateTime.now());

        return response;
    }

    // ==================== 좋아요 토글 ====================

    public DiscussDto toggleLike(Long postId, Long userId) {

        // 1) 게시글 조회
        Discuss discuss = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시글이 없습니다. postId=" + postId));

        // 2) 이미 좋아요 눌렀는지 확인
        boolean alreadyLiked = discussLikeRepository
                .existsByIdPostIdAndIdLikerId(postId, userId);

        boolean viewerLiked;

        if (alreadyLiked) {
            // 좋아요 취소
            discussLikeRepository.deleteByIdPostIdAndIdLikerId(postId, userId);
            discuss.setLikeCount(discuss.getLikeCount() - 1);
            viewerLiked = false;
        } else {
            // 좋아요 추가
            DiscussLike like = DiscussLike.of(postId, userId);
            discussLikeRepository.save(like);
            discuss.setLikeCount(discuss.getLikeCount() + 1);
            viewerLiked = true;
        }

        // 3) DTO 변환 (authorName + viewerLiked + message)
        String authorName = resolveAuthorName(discuss.getAuthorId());
        DiscussDto dto = DiscussDto.fromEntity(discuss, authorName, viewerLiked);
        dto.setMessage(viewerLiked
                ? "❤️ 좋아요가 추가되었습니다."
                : "💔 좋아요가 취소되었습니다.");

        return dto;
    }
}
