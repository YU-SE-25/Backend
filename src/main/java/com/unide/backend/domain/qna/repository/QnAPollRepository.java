package com.unide.backend.domain.qna.repository;

import com.unide.backend.domain.qna.entity.QnAPoll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnAPollRepository extends JpaRepository<QnAPoll, Long> {
}
