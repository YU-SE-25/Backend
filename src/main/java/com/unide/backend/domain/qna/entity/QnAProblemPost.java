package com.unide.backend.domain.qna.entity;

import com.unide.backend.domain.problems.entity.Problems;
import jakarta.persistence.*;
import lombok.*;

@Getter              // ✅ 중요: getProblem() 필요
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder             // ✅ QnAProblemPost.builder() 에러 해결
@Entity
public class QnAProblemPost {

    @EmbeddedId
    private QnAProblemPostId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id", nullable = false)
    private QnA qna;   // qna 테이블

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("problemId")
    @JoinColumn(name = "problem_id", nullable = false)
    private Problems problem;  // problems 테이블
}
