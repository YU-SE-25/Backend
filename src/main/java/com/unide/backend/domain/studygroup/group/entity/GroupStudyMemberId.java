package com.unide.backend.domain.studygroup.group.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
public class GroupStudyMemberId implements Serializable {
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "member_id")
    private Long memberId;
}
