package com.unide.backend.domain.studygroup.discuss.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussPollVote;

public interface st_DiscussPollVoteRepository extends JpaRepository<st_DiscussPollVote, Long> {

    boolean existsByPoll_PollIdAndVoterId(Long pollId, Long voterId);
}
