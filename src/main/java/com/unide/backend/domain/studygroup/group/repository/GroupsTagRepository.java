package com.unide.backend.domain.studygroup.group.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unide.backend.domain.studygroup.group.entity.GroupsTag;
import com.unide.backend.domain.studygroup.group.entity.GroupsTagId;

public interface GroupsTagRepository extends JpaRepository<GroupsTag, GroupsTagId> {

    void deleteByGroupId(Long groupId);

    List<GroupsTag> findByGroupId(Long groupId);
}
