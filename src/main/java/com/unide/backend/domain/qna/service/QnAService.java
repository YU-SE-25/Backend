package com.unide.backend.domain.qna.service;

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

import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.qna.dto.QnADto;
import com.unide.backend.domain.qna.dto.QnAProblemDto;
import com.unide.backend.domain.qna.entity.QnA;
import com.unide.backend.domain.qna.entity.QnALike;
import com.unide.backend.domain.qna.repository.QnALikeRepository;
import com.unide.backend.domain.qna.repository.QnARepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class QnAService {

    private final QnARepository qnaRepository;
    private final QnAProblemPostService qnaProblemPostService;
    private final UserRepository userRepository; 
    private final QnALikeRepository qnaLikeRepository;

    // ===== 목록 조회 =====
    @Transactional(readOnly = true)
    public List<QnADto> getQnAList(int pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 10,
                Sort.by(Sort.Direction.DESC, "id"));   // QnA PK 필드명

        Page<QnA> page = qnaRepository.findAll(pageRequest);

        return page.stream()
                .map(qna -> {
                    Problems linked = qnaProblemPostService
                            .getLinkedProblem(qna.getId())
                            .orElse(null);

                    QnAProblemDto problemDto = QnAProblemDto.fromEntity(linked);
                    return QnADto.fromEntity(qna, problemDto);
                })
                .collect(Collectors.toList());
    }

    // ===== 단건 조회 =====
    @Transactional(readOnly = true)
    public QnADto getQnA(Long postId) {
        QnA qna = qnaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 QnA글이 없습니다. postId=" + postId));

        Problems linked = qnaProblemPostService.getLinkedProblem(postId).orElse(null);
        QnAProblemDto problemDto = QnAProblemDto.fromEntity(linked);

        return QnADto.fromEntity(qna, problemDto);
    }


    // 생성
public QnADto createQnA(QnADto dto, Long authorId) {

    // 1) 작성자 User 실제 엔티티 조회
    User author = userRepository.findById(authorId)
            .orElseThrow(() ->
                    new IllegalArgumentException("작성자를 찾을 수 없습니다. authorId=" + authorId));

    // 2) QnA 엔티티 생성
    QnA qna = QnA.builder()
            .author(author)                    
            .anonymous(dto.isAnonymous())
            .title(dto.getTitle())
            .contents(dto.getContents())
            .privatePost(dto.isPrivatePost())
            .likeCount(0)
            .commentCount(0)
            .build();

    QnA savedQnA = qnaRepository.save(qna);
    return QnADto.fromEntity(savedQnA);
}



    // 수정
    public QnADto updateQnA(Long postId, QnADto dto) {
        QnA qna = qnaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 QnA글이 없습니다. postId=" + postId));

        qna.setAnonymous(dto.isAnonymous());
        qna.setTitle(dto.getTitle());
        qna.setContents(dto.getContents());
        qna.setPrivatePost(dto.isPrivatePost());

        return QnADto.fromEntity(qnaRepository.save(qna));
    }


    // 삭제
    public void deleteQnA(Long postId) {
        QnA qna = qnaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 QnA글이 없습니다. postId=" + postId));
        qnaRepository.delete(qna);
    }


    // 검색
    @Transactional(readOnly = true)
    public List<QnADto> searchQnAs(String keyword) {
        return qnaRepository
                .findByTitleContainingIgnoreCaseOrContentsContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(QnADto::fromEntity)
                .collect(Collectors.toList());
    }
    
    //첨부파일 첨가
    @Transactional
    public Map<String, Object> attachFile(Long postId, String fileUrl) {

        QnA post = qnaRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시물이 없습니다. postId=" + postId));

        post.setAttachmentUrl(fileUrl);   // 첨부 URL 저장

        Map<String, Object> response = new HashMap<>();
        response.put("message", "첨부파일이 등록되었습니다.");
        response.put("post_id", postId);
        response.put("updated_at", LocalDateTime.now());

        return response;
    }
        // ===== QnA 게시글 좋아요 토글 =====
public QnADto toggleLike(Long postId, Long userId) {

    // 1) 게시글 조회
    QnA qna = qnaRepository.findById(postId)
            .orElseThrow(() ->
                    new IllegalArgumentException("해당 게시글이 없습니다. postId=" + postId));

    // 2) 좋아요 확인
    boolean alreadyLiked = qnaLikeRepository
            .existsByIdPostIdAndIdLikerId(postId, userId);

    boolean viewerLiked;

    if (alreadyLiked) {
        // 👍 좋아요 취소
        qnaLikeRepository.deleteByIdPostIdAndIdLikerId(postId, userId);
        qna.setLikeCount(qna.getLikeCount() - 1);
        viewerLiked = false;
    } else {
        // ❤️ 좋아요 추가
        QnALike like = QnALike.of(postId, userId);
        qnaLikeRepository.save(like);
        qna.setLikeCount(qna.getLikeCount() + 1);
        viewerLiked = true;
    }

    // 3) DTO 생성 (viewerLiked 포함)
    QnADto dto = QnADto.fromEntity(qna, null, viewerLiked);

    // 4) 좋아요 메시지 추가 💗
    dto.setMessage(viewerLiked ? "❤️ 좋아요가 추가되었습니다." 
                               : "💔 좋아요가 취소되었습니다.");

    return dto;
}

    
}
