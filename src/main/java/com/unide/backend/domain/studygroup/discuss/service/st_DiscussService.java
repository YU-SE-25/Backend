package com.unide.backend.domain.studygroup.discuss.service;

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

import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussDto;
import com.unide.backend.domain.studygroup.discuss.entity.st_Discuss;
import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussLike;
import com.unide.backend.domain.studygroup.discuss.repository.st_DiscussLikeRepository;
import com.unide.backend.domain.studygroup.discuss.repository.st_DiscussRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class st_DiscussService {

    private final st_DiscussRepository discussRepository;
    private final st_DiscussLikeRepository likeRepository;

    // ==========================
    // 📌 목록 조회 (그룹별)
    // ==========================
    @Transactional(readOnly = true)
    public List<st_DiscussDto> getDiscussList(Long groupId, int pageNum) {

        PageRequest pageRequest = PageRequest.of(
                pageNum - 1,
                10,
                Sort.by(Sort.Direction.DESC, "postId")
        );

        Page<st_Discuss> page =
                discussRepository.findByGroupId(groupId, pageRequest);

        return page.stream()
                .map(st_DiscussDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ==========================
    // 📌 단건 조회
    // ==========================
    @Transactional(readOnly = true)
    public st_DiscussDto getDiscuss(Long groupId, Long postId) {

        st_Discuss discuss = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시글이 없습니다. postId=" + postId));

        if (!discuss.getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("해당 그룹의 게시글이 아닙니다.");
        }

        return st_DiscussDto.fromEntity(discuss);
    }

    // ==========================
    // 📌 생성
    // ==========================
    public st_DiscussDto createDiscuss(Long groupId, st_DiscussDto dto, Long authorId) {

        st_Discuss discuss = st_Discuss.builder()
                .groupId(groupId)
                .authorId(authorId)
                .anonymous(dto.isAnonymous())
                .title(dto.getTitle())
                .contents(dto.getContents())
                .privatePost(dto.isPrivatePost())
                .likeCount(0)
                .commentCount(0)
                .build();

        st_Discuss saved = discussRepository.save(discuss);
        return st_DiscussDto.fromEntity(saved);
    }

    // ==========================
    // 📌 수정
    // ==========================
    public st_DiscussDto updateDiscuss(Long groupId, Long postId, st_DiscussDto dto) {

        st_Discuss discuss = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시글이 없습니다. postId=" + postId));

        if (!discuss.getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("해당 그룹의 게시글이 아닙니다.");
        }

        discuss.setTitle(dto.getTitle());
        discuss.setContents(dto.getContents());
        discuss.setAnonymous(dto.isAnonymous());
        discuss.setPrivatePost(dto.isPrivatePost());

        return st_DiscussDto.fromEntity(discuss);
    }

    // ==========================
    // 📌 삭제
    // ==========================
    public void deleteDiscuss(Long groupId, Long postId) {

        st_Discuss discuss = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시글이 없습니다. postId=" + postId));

        if (!discuss.getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("해당 그룹의 게시글이 아닙니다.");
        }

        discussRepository.delete(discuss);
    }

    // ==========================
    // 📌 검색 (그룹 내에서만)
    // ==========================
    @Transactional(readOnly = true)
    public List<st_DiscussDto> searchDiscusses(Long groupId, String keyword) {

        List<st_Discuss> list = discussRepository
                .findByGroupIdAndTitleContainingIgnoreCaseOrGroupIdAndContentsContainingIgnoreCase(
                        groupId, keyword,
                        groupId, keyword
                );

        return list.stream()
                .map(st_DiscussDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ==========================
    // 📌 첨부파일 등록
    // ==========================
    @Transactional
    public Map<String, Object> attachFile(Long groupId, Long postId, String fileUrl) {

        st_Discuss post = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시물이 없습니다. postId=" + postId));

        if (!post.getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("해당 그룹 게시물이 아닙니다.");
        }

        post.setAttachmentUrl(fileUrl);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "첨부파일이 등록되었습니다.");
        response.put("post_id", postId);
        response.put("updated_at", LocalDateTime.now());

        return response;
    }

    // ==========================
    // 📌 좋아요 토글
    // ==========================
    public st_DiscussDto toggleLike(Long groupId, Long postId, Long userId) {

        st_Discuss discuss = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시글이 없습니다. postId=" + postId));

        if (!discuss.getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("해당 그룹 게시글이 아닙니다.");
        }

        boolean alreadyLiked =
                likeRepository.existsByIdPostIdAndIdLikerIdAndIdGroupId(postId, userId, groupId);

        boolean viewerLiked;

        if (alreadyLiked) {
            likeRepository.deleteByIdPostIdAndIdLikerIdAndIdGroupId(postId, userId, groupId);
            discuss.setLikeCount(discuss.getLikeCount() - 1);
            viewerLiked = false;
        } else {

            st_DiscussLike like = st_DiscussLike.of(postId, userId, groupId);
            likeRepository.save(like);

            discuss.setLikeCount(discuss.getLikeCount() + 1);
            viewerLiked = true;
        }

        st_DiscussDto dto = st_DiscussDto.fromEntity(discuss, viewerLiked);
        dto.setMessage(viewerLiked ? "❤️ 좋아요 추가" : "💔 좋아요 취소");

        return dto;
    }
}
