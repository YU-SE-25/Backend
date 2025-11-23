package com.unide.backend.domain.qna.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor   // ✅ 이거 추가!
@EqualsAndHashCode
@Embeddable
public class QnAProblemPostId implements Serializable {

    private Long postId;
    private Long problemId;
}
