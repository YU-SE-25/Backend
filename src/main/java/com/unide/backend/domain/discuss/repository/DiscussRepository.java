package com.unide.backend.domain.discuss.repository;

import com.unide.backend.domain.discuss.entity.Discuss;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscussRepository extends JpaRepository<Discuss, Long> {

    Page<Discuss> findAll(Pageable pageable);

    List<Discuss> findByTitleContainingIgnoreCaseOrContentsContainingIgnoreCase(
            String titleKeyword,
            String contentsKeyword
    );
}
