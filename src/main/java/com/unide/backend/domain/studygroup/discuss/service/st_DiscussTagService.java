package com.unide.backend.domain.studygroup.discuss.service;

import com.unide.backend.domain.studygroup.discuss.entity.st_DiscussTag;
import com.unide.backend.domain.studygroup.discuss.repository.st_DiscussTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class st_DiscussTagService {

    private final st_DiscussTagRepository discussTagRepository;

    // 태그 등록 (기존 태그 싹 지우고 다시 저장)
    public void addTagsToPost(Long groupId, Long postId, List<Long> tagIds) {

        discussTagRepository.deleteByGroupIdAndPostId(groupId, postId);

        for (Long tagId : tagIds) {
            st_DiscussTag entity = new st_DiscussTag(postId, tagId, groupId, true);
            discussTagRepository.save(entity);
        }
    }

    // 태그 조회
    @Transactional(readOnly = true)
    public List<Long> getTagsByPost(Long groupId, Long postId) {
        return discussTagRepository.findByGroupIdAndPostId(groupId, postId)
                .stream()
                .map(st_DiscussTag::getTagId)
                .toList();
    }
}
