// 엔터티가 생성되거나 수정될 때 시간을 자동으로 기록하기 위한 역할

package com.unide.backend.common.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 이 클래스를 상속하는 엔터티의 필드만 매핑함
@EntityListeners(AuditingEntityListener.class) // 시간 자동 감지 기능 활성화
public abstract class BaseTimeEntity {
    @CreatedDate // 생성 시간
    private LocalDateTime createdAt;

    @LastModifiedDate // 수정 시간
    private LocalDateTime updatedAt;
}