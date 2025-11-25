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
import com.unide.backend.domain.discuss.repository.DiscussRepository;
import com.unide.backend.domain.discuss.repository.DiscussLikeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussService {

    private final DiscussRepository discussRepository;
    private final DiscussLikeRepository discussLikeRepository;


    // 💡 참고: toDto private 메서드는 DiscussDto.fromEntity()로 대체되었습니다.
    // DTO 변환 로직은 DTO 클래스 내부에 정의하는 것이 더 좋습니다.

    // ========== 목록 조회 ==========
    @Transactional(readOnly = true)
    public List<DiscussDto> getDiscussList(int pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 10,
                Sort.by(Sort.Direction.DESC, "postId"));

        Page<Discuss> page = discussRepository.findAll(pageRequest);

        return page.stream()
                .map(DiscussDto::fromEntity) // DTO의 정적 팩토리 메서드 사용
                .collect(Collectors.toList());
    }

    // ========== 단건 조회 ==========
    @Transactional(readOnly = true)
    public DiscussDto getDiscuss(Long postId) {
        Discuss discuss = discussRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 토론글이 없습니다. postId=" + postId));

        return DiscussDto.fromEntity(discuss);
    }

    // ========== 생성 ==========
    public DiscussDto createDiscuss(DiscussDto dto,Long authorId) {
        // Discuss 엔티티에 Builder 패턴이 적용되어 있다는 가정 하에 작성됩니다.
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
        return DiscussDto.fromEntity(saved);
    }

    // ========== 수정 ==========
    public DiscussDto updateDiscuss(Long postId, DiscussDto dto) {
        Discuss discuss = discussRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 토론글이 없습니다. postId=" + postId));

        discuss.setTitle(dto.getTitle());
        discuss.setContents(dto.getContents());
        discuss.setAnonymous(dto.isAnonymous());
        discuss.setPrivatePost(dto.isPrivatePost());
        // Jpa는 트랜잭션(@Transactional) 내에서 엔티티가 변경되면 자동으로 DB에 반영합니다.

        return DiscussDto.fromEntity(discuss);
    }

    // ========== 삭제 ==========
    public void deleteDiscuss(Long postId) {
        if (!discussRepository.existsById(postId)) {
            throw new IllegalArgumentException("해당 토론글이 없습니다. postId=" + postId);
        }
        discussRepository.deleteById(postId);
    }

    // ========== 검색 ==========
    @Transactional(readOnly = true)
    public List<DiscussDto> searchDiscusses(String keyword) {
        List<Discuss> list = discussRepository
                .findByTitleContainingIgnoreCaseOrContentsContainingIgnoreCase(keyword, keyword);

        return list.stream()
                .map(DiscussDto::fromEntity)
                .collect(Collectors.toList());
    }
    //첨부파일 첨가
    @Transactional
    public Map<String, Object> attachFile(Long postId, String fileUrl) {

        Discuss post = discussRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시물이 없습니다. postId=" + postId));

        post.setAttachmentUrl(fileUrl);   // 첨부 URL 저장

        Map<String, Object> response = new HashMap<>();
        response.put("message", "첨부파일이 등록되었습니다.");
        response.put("post_id", postId);
        response.put("updated_at", LocalDateTime.now());

        return response;
    }
    //좋아요// ===== 토론 게시글 좋아요 토글 =====
public DiscussDto toggleLike(Long postId, Long userId) {

    // 1) 게시글 조회
    Discuss discuss = discussRepository.findById(postId)
            .orElseThrow(() ->
                    new IllegalArgumentException("해당 게시글이 없습니다. postId=" + postId));

    // 2) 내가 이미 좋아요 눌렀는지 확인
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

    // 3) DTO로 반환 (viewerLiked까지 세팅)
   // 3) DTO로 반환 (viewerLiked + message까지 세팅)
DiscussDto dto = DiscussDto.fromEntity(discuss, viewerLiked);
dto.setMessage(viewerLiked ? "❤️ 좋아요가 추가되었습니다." 
                           : "💔 좋아요가 취소되었습니다.");
return dto;


}

}