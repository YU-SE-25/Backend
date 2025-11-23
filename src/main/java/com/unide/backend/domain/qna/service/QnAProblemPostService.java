package com.unide.backend.domain.qna.service;

import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.problems.repository.ProblemsRepository;
import com.unide.backend.domain.qna.entity.QnA;
import com.unide.backend.domain.qna.entity.QnAProblemPost;
import com.unide.backend.domain.qna.entity.QnAProblemPostId;
import com.unide.backend.domain.qna.repository.QnAProblemPostRepository;
import com.unide.backend.domain.qna.repository.QnARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QnAProblemPostService {

    private final QnARepository qnaRepository;
    private final ProblemsRepository problemsRepository;
    private final QnAProblemPostRepository qnaProblemPostRepository;

    // 게시글에 문제 연동 (게시글당 1개만 유지)
    @Transactional
    public void linkProblemToPost(Long postId, Long problemId) {
        QnA qna = qnaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. postId=" + postId));

        Problems problem = problemsRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문제입니다. problemId=" + problemId));

        // 게시글당 문제 1개만 허용 → 기존 관계 있으면 삭제
        qnaProblemPostRepository.deleteByQna_Id(postId);

        QnAProblemPost entity = QnAProblemPost.builder()
                .id(new QnAProblemPostId(postId, problemId))
                .qna(qna)
                .problem(problem)
                .build();

        qnaProblemPostRepository.save(entity);
    }

    // 연동 해제
    @Transactional
    public void unlinkProblemFromPost(Long postId) {
        qnaProblemPostRepository.deleteByQna_Id(postId);
    }

    // 게시글에 연동된 문제 조회
    @Transactional(readOnly = true)
    public Optional<Problems> getLinkedProblem(Long postId) {
        return qnaProblemPostRepository.findByQna_Id(postId)
                .map(QnAProblemPost::getProblem);
    }
}
