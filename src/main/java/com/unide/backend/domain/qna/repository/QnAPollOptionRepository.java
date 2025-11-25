package com.unide.backend.domain.qna.repository;

import com.unide.backend.domain.qna.entity.QnAPoll;
import com.unide.backend.domain.qna.entity.QnAPollOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnAPollOptionRepository extends JpaRepository<QnAPollOption, Long> {
    List<QnAPollOption> findByPoll(QnAPoll poll);
}
