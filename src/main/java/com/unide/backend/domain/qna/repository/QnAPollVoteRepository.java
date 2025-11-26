package com.unide.backend.domain.qna.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.qna.entity.QnAPoll;
import com.unide.backend.domain.qna.entity.QnAPollVote;

public interface QnAPollVoteRepository extends JpaRepository<QnAPollVote, Long> {

    boolean existsByPollAndVoterId(QnAPoll poll, Long voterId);

    List<QnAPollVote> findByPollAndVoterId(QnAPoll poll, Long voterId);
}
