package com.unide.backend.domain.studygroup.discuss.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.discuss.entity.st_Discuss;

public interface st_DiscussRepository extends JpaRepository<st_Discuss, Long> {

    // 그룹별 게시글 페이징 조회
    Page<st_Discuss> findByGroupId(Long groupId, Pageable pageable);

    // 그룹 안에서 제목/내용 검색
    // (groupId = ?1 AND title like ?) OR (groupId = ?3 AND contents like ?)
    List<st_Discuss> findByGroupIdAndTitleContainingIgnoreCaseOrGroupIdAndContentsContainingIgnoreCase(
            Long groupIdForTitle, String titleKeyword,
            Long groupIdForContents, String contentsKeyword
    );
}
