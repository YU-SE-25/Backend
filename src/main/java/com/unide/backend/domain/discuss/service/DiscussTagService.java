package com.unide.backend.domain.discuss.service;

import com.unide.backend.domain.discuss.entity.DiscussTag;
import com.unide.backend.domain.discuss.repository.DiscussTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class DiscussTagService {

    private final DiscussTagRepository discussTagRepository;

    public void addTagsToPost(Long postId, List<Long> tagIds) {
        // 1) 기존 태그들 삭제
        discussTagRepository.deleteByPostId(postId);

        // 2) 새 태그들 저장
        for (Long tagId : tagIds) {
            DiscussTag entity = new DiscussTag(postId, tagId);
            discussTagRepository.save(entity);
        }
    }

    public List<Long> getTagsByPost(Long postId) {
        List<DiscussTag> entities = discussTagRepository.findByPostId(postId);

        return entities.stream()
                .map(DiscussTag::getTagId)
                .toList();
    }
}
