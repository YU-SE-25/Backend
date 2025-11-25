package com.unide.backend.domain.qna.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "qna_poll_option")
public class QnAPollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private QnAPoll poll;

    @Column(name = "content", nullable = false, length = 200)
    private String content;

    @Column(name = "label", nullable = false)   // 🔥 DB에 있는 label 컬럼
    private String label;

    protected QnAPollOption() {}

     public QnAPollOption(QnAPoll poll, String label, String content) {
        this.poll = poll;
        this.label = label;
        this.content = content;
    }

    public Long getId() { return id; }
    public QnAPoll getPoll() { return poll; }
    public String getContent() { return content; }
}
