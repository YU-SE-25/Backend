package com.unide.backend.domain.qna.service;

import com.unide.backend.domain.qna.dto.QnADto;
import com.unide.backend.domain.qna.entity.QnA;
import com.unide.backend.domain.qna.repository.QnARepository;
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
public class QnAService {
    private final QnARepository qnaRepository;

    // ========== 목록 조회 ==========
    @Transactional(readOnly = true)
    public List<QnADto> getQnAList(int pageNum) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 10,
                Sort.by(Sort.Direction.DESC, "postId"));

        Page<QnA> page = qnaRepository.findAll(pageRequest);

        return page.stream()
                .map(QnADto::fromEntity) // DTO의 정적 팩토리 메서드 사용
                .collect(Collectors.toList());
    }
    // ========== 단건 조회 ==========
    @Transactional(readOnly = true) 
    public QnADto getQnA(Long postId) {
        QnA qna = qnaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 QnA글이 없습니다. postId=" + postId));

        return QnADto.fromEntity(qna);
    }
    // ========== 생성 ==========
    public QnADto createQnA(QnADto dto,Long authorId) {
        // QnA 엔티티에 Builder 패턴이 적용되어 있다는 가정 하에 작성됩니다.
        QnA qna = QnA.builder()
                .authorId(authorId)
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
    // ========== 수정 ==========
    public QnADto updateQnA(Long postId, QnADto dto) {
        QnA qna = qnaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 QnA글이 없습니다. postId=" + postId));

        qna.setAnonymous(dto.isAnonymous());
        qna.setTitle(dto.getTitle());
        qna.setContents(dto.getContents());
        qna.setPrivatePost(dto.isPrivatePost());

        QnA updatedQnA = qnaRepository.save(qna);
        return QnADto.fromEntity(updatedQnA);
    }
    // ========== 삭제 ==========
    public void deleteQnA(Long postId) {
        QnA qna = qnaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 QnA글이 없습니다. postId=" + postId));

        qnaRepository.delete(qna);
    }
    // ========== 검색 ==========
    @Transactional(readOnly = true)
    public List<QnADto> searchQnAs(String keyword) {
        List<QnA> qnaList = qnaRepository.findByTitleContainingIgnoreCaseOrContentsContainingIgnoreCase(keyword, keyword);

        return qnaList.stream()
                .map(QnADto::fromEntity)
                .collect(Collectors.toList());
    }

}
