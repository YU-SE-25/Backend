package com.unide.backend.domain.discuss.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dis_poll")
public class DisPoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poll_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private Discuss post;

    // 🔹 API 명세서에 option1~3 문자열만 있어서 그대로 매핑
    @Column(name = "option1", length = 100)
    private String option1;

    @Column(name = "option2", length = 100)
    private String option2;

    @Column(name = "option3", length = 100)
    private String option3;
}
