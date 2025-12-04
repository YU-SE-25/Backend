package com.unide.backend.domain.discuss.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.discuss.entity.DiscussPoll;
import com.unide.backend.domain.discuss.entity.DiscussPollOption;

public interface DiscussPollOptionRepository extends JpaRepository<DiscussPollOption, Long> {

    List<DiscussPollOption> findByPoll(DiscussPoll poll);

    // 🔥 poll_id + label 로 옵션 한 개 찾기
    Optional<DiscussPollOption> findByPoll_IdAndLabel(Long pollId, String label);
}
