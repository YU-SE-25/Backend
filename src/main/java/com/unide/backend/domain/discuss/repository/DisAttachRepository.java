package com.unide.backend.domain.discuss.repository;

import com.unide.backend.domain.discuss.entity.DisAttach;
import com.unide.backend.domain.discuss.entity.Discuss;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisAttachRepository extends JpaRepository<DisAttach, Long> {

    List<DisAttach> findByPost(Discuss post);
}
