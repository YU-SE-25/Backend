package com.unide.backend.domain.discuss.service;

import com.unide.backend.domain.discuss.dto.DiscussDto;
import com.unide.backend.domain.discuss.entity.Discuss;
import com.unide.backend.domain.discuss.repository.DiscussRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussService {

    private final DiscussRepository discussRepository;

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
}