package com.unide.backend.domain.discuss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.discuss.entity.DiscussPoll;
import com.unide.backend.domain.discuss.entity.DiscussPollVote;

public interface DiscussPollVoteRepository extends JpaRepository<DiscussPollVote, Long> {

    // 단일/중복 투표 여부 체크용
    boolean existsByPollAndVoterId(DiscussPoll poll, Long voterId);

    // (필요하면) 특정 유저가 특정 투표에서 한 모든 투표 조회용
    List<DiscussPollVote> findByPollAndVoterId(DiscussPoll poll, Long voterId);
}
