package com.unide.backend.domain.discuss.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.discuss.entity.DiscussPoll;
import com.unide.backend.domain.discuss.entity.DiscussPollOption;


public interface DiscussPollOptionRepository extends JpaRepository<DiscussPollOption, Long> {
    List<DiscussPollOption> findByPoll(DiscussPoll poll);}