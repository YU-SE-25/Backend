package com.unide.backend.domain.qna.repository;

import com.unide.backend.domain.qna.entity.QnAProblemPost;
import com.unide.backend.domain.qna.entity.QnAProblemPostId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QnAProblemPostRepository extends JpaRepository<QnAProblemPost, QnAProblemPostId> {

    // QnAProblemPost.qna.id 로 삭제
    void deleteByQna_Id(Long postId);

    // QnAProblemPost.qna.id 로 조회
    Optional<QnAProblemPost> findByQna_Id(Long postId);
}
