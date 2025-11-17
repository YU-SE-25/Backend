package com.unide.backend.domain.discuss.repository;

import com.unide.backend.domain.discuss.entity.DisPoll;
import com.unide.backend.domain.discuss.entity.Discuss;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisPollRepository extends JpaRepository<DisPoll, Long> {

    Optional<DisPoll> findByPost(Discuss post);

    boolean existsByPost(Discuss post);
}
