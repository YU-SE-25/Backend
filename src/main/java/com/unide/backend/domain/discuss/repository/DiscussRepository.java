package com.unide.backend.domain.discuss.repository;

import com.unide.backend.domain.discuss.entity.Discuss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussRepository extends JpaRepository<Discuss, Long> {
}
