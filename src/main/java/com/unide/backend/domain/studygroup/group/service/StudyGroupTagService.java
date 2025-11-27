package com.unide.backend.domain.studygroup.group.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unide.backend.domain.studygroup.group.entity.StudyGroupTag;
import com.unide.backend.domain.studygroup.group.entity.GroupsTag;
import com.unide.backend.domain.studygroup.group.repository.StudyGroupTagRepository;
import com.unide.backend.domain.studygroup.group.repository.GroupsTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyGroupTagService {

    private final StudyGroupTagRepository studyGroupTagRepository;
    private final GroupsTagRepository groupsTagRepository;

    /** 그룹 태그 설정 (기존 삭제 후 재등록) */
    public void setTagsForGroup(Long groupId, List<Long> tagIds) {

        // 기존 태그 매핑 삭제
        groupsTagRepository.deleteByGroupId(groupId);

        // 태그 없으면 끝
        if (tagIds == null || tagIds.isEmpty()) return;

        // 새 태그 매핑 등록
        for (Long tagId : tagIds) {
            GroupsTag mapping = new GroupsTag(groupId, tagId);
            groupsTagRepository.save(mapping);
        }
    }

    /** 특정 그룹의 태그 id 목록 조회 */
    @Transactional(readOnly = true)
    public List<Long> getTagIdsByGroup(Long groupId) {

        List<GroupsTag> mappings = groupsTagRepository.findByGroupId(groupId);

        return mappings.stream()
                .map(GroupsTag::getTagId)
                .toList();
    }

    /** 전체 태그 조회 */
    @Transactional(readOnly = true)
    public List<StudyGroupTag> getAllTags() {
        return studyGroupTagRepository.findAll();
    }

    /** 태그 생성 (선택 기능) */
   public StudyGroupTag createTag(String tagName) {
    StudyGroupTag tag = StudyGroupTag.builder()
            .tagName(tagName)
            .build();
    return studyGroupTagRepository.save(tag);
}


}
