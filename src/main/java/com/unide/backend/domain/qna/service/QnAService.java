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

import com.unide.backend.domain.mypage.service.StatsService;
import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.qna.dto.QnADto;
import com.unide.backend.domain.qna.dto.QnAPollCreateRequest;
import com.unide.backend.domain.qna.dto.QnAPollResponse;
import com.unide.backend.domain.qna.dto.QnAPollVoteRequest;
import com.unide.backend.domain.qna.dto.QnAPollVoteResponse;
import com.unide.backend.domain.qna.dto.QnAProblemDto;
import com.unide.backend.domain.qna.entity.QnA;
import com.unide.backend.domain.qna.entity.QnALike;
import com.unide.backend.domain.qna.repository.QnALikeRepository;
import com.unide.backend.domain.qna.repository.QnARepository;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.domain.user.repository.UserRepository;
import com.unide.backend.global.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class QnAService {

    private final QnARepository qnaRepository;
    private final QnAProblemPostService qnaProblemPostService;
    private final UserRepository userRepository;
    private final QnALikeRepository qnaLikeRepository;
    private final StatsService statsService;

    // ===== 목록 조회 =====

    // 기존 버전: 다른 코드 호환용
    public PageResponse<QnADto> getQnAList(int pageNum) {
        return getQnAList(pageNum, null);
    }

    // 새 버전: viewerId 까지 받아서 viewerLiked 채워줌
    @Transactional(readOnly = true)
    public PageResponse<QnADto> getQnAList(int pageNum, Long viewerId) {
        PageRequest pageRequest = PageRequest.of(
                pageNum - 1,
                10,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<QnA> page = qnaRepository.findAll(pageRequest);

        List<QnADto> content = page.stream()
                .map(qna -> {
                    Problems linked = qnaProblemPostService
                            .getLinkedProblem(qna.getId())
                            .orElse(null);
                    QnAProblemDto problemDto = QnAProblemDto.fromEntity(linked);

                    boolean viewerLiked = false;
                    if (viewerId != null) {
                        viewerLiked = qnaLikeRepository
                                .existsByIdPostIdAndIdLikerId(qna.getId(), viewerId);
                    }

                    // fromEntity 오버로드: (qna, problemDto, viewerLiked)
                    return QnADto.fromEntity(qna, problemDto, viewerLiked);
                })
                .toList();

        return PageResponse.<QnADto>builder()
                .content(content)
                .page(pageNum)                 // 1-based
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    // ===== 단건 조회 =====

    @Transactional(readOnly = true)
    public QnADto getQnA(Long postId) {
        return getQnA(postId, null);
    }

    @Transactional(readOnly = true)
    public QnADto getQnA(Long postId, Long viewerId) {
        QnA qna = qnaRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 QnA글이 없습니다. postId=" + postId));

        Problems linked = qnaProblemPostService.getLinkedProblem(postId).orElse(null);
        QnAProblemDto problemDto = QnAProblemDto.fromEntity(linked);

        boolean viewerLiked = false;
        if (viewerId != null) {
            viewerLiked = qnaLikeRepository
                    .existsByIdPostIdAndIdLikerId(postId, viewerId);
        }

        return QnADto.fromEntity(qna, problemDto, viewerLiked);
    }

    // ===== 생성 =====
    public QnADto createQnA(QnADto dto, Long authorId) {

        User author = userRepository.findById(authorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("작성자를 찾을 수 없습니다. authorId=" + authorId));

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
        // 생성 직후 viewerLiked 정보는 없으니 false 로
        return QnADto.fromEntity(savedQnA, null, false);
    }

    // ===== 수정 =====
    public QnADto updateQnA(Long postId, QnADto dto) {
        QnA qna = qnaRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 QnA글이 없습니다. postId=" + postId));

        qna.setAnonymous(dto.isAnonymous());
        qna.setTitle(dto.getTitle());
        qna.setContents(dto.getContents());
        qna.setPrivatePost(dto.isPrivatePost());

        return QnADto.fromEntity(qna, null, false);
    }

    // ===== 삭제 =====
    public void deleteQnA(Long postId) {
        QnA qna = qnaRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 QnA글이 없습니다. postId=" + postId));
        qnaRepository.delete(qna);
    }

    // ===== 검색 =====
    @Transactional(readOnly = true)
    public List<QnADto> searchQnAs(String keyword) {
        return qnaRepository
                .findByTitleContainingIgnoreCaseOrContentsContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(QnADto::fromEntity)   // 검색에서는 viewerLiked 안 씀
                .collect(Collectors.toList());
    }

    // ===== 첨부파일 첨가 =====
    @Transactional
    public Map<String, Object> attachFile(Long postId, String fileUrl) {

        QnA post = qnaRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시물이 없습니다. postId=" + postId));

        post.setAttachmentUrl(fileUrl);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "첨부파일이 등록되었습니다.");
        response.put("post_id", postId);
        response.put("updated_at", LocalDateTime.now());

        return response;
    }

    // ===== QnA 게시글 좋아요 토글 =====
    public QnADto toggleLike(Long postId, Long userId) {

        QnA qna = qnaRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 게시글이 없습니다. postId=" + postId));

        boolean alreadyLiked = qnaLikeRepository
                .existsByIdPostIdAndIdLikerId(postId, userId);

        boolean viewerLiked;

        if (alreadyLiked) {
            qnaLikeRepository.deleteByIdPostIdAndIdLikerId(postId, userId);
            qna.setLikeCount(qna.getLikeCount() - 1);
            viewerLiked = false;
        } else {
            QnALike like = QnALike.of(postId, userId);
            qnaLikeRepository.save(like);
            qna.setLikeCount(qna.getLikeCount() + 1);
            viewerLiked = true;

            Long authorId = qna.getAuthor().getId();
            statsService.onQnaPostLiked(authorId);
        }

        QnADto dto = QnADto.fromEntity(qna, null, viewerLiked);
        dto.setMessage(viewerLiked
                ? "❤️ 좋아요가 추가되었습니다."
                : "💔 좋아요가 취소되었습니다.");

        return dto;
    }

    // ===== 아래 두 개는 지금 PollService 따로 있으니까 그대로 두거나 지워도 무방 =====
    public QnAPollResponse createPoll(Long postId, Long authorId, QnAPollCreateRequest request) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public QnAPollVoteResponse vote(Long voterId, Long pollId, QnAPollVoteRequest request) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
