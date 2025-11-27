package com.unide.backend.domain.studygroup.group.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupsTagId implements Serializable {

    private Long groupId;
    private Long tagId;
}
