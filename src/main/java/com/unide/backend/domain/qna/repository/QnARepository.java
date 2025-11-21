package com.unide.backend.domain.qna.repository;


import com.unide.backend.domain.qna.entity.QnA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QnARepository extends JpaRepository<QnA, Long> {
    Page<QnA> findAll(Pageable pageable);

    List<QnA> findByTitleContainingIgnoreCaseOrContentsContainingIgnoreCase(
            String titleKeyword,
            String contentsKeyword
            
    );}

