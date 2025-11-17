package com.unide.backend.domain.discuss.entity;


import com.unide.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dis_attach")
public class DisAttach extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attach_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Discuss post;

    @Column(name = "contents", nullable = false, length = 500)
    private String contents;   // 🔹 API 명세: "contents": "첨부파일 url"
}
