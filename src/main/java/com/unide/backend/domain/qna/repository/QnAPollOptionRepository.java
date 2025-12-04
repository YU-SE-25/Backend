package com.unide.backend.domain.qna.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.qna.entity.QnAPoll;
import com.unide.backend.domain.qna.entity.QnAPollOption;

public interface QnAPollOptionRepository extends JpaRepository<QnAPollOption, Long> {

    // 기존 메서드 (필요하면 계속 사용)
    List<QnAPollOption> findByPoll(QnAPoll poll);

    // ✅ poll_id + label 로 옵션 1개 찾기
    // 엔티티에 pollId가 직접 있는 게 아니라 poll.id 라서
    // 메서드 이름을 Poll_Id 로 써야 함
    Optional<QnAPollOption> findByPoll_IdAndLabel(Long pollId, String label);
}
