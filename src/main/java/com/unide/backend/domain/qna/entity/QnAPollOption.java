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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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

    @Column(name = "label", nullable = false)
    private String label;          // "1", "2", ...

    @Column(name = "content", nullable = false, length = 200)
    private String content;        // 보기 내용

    @Column(name = "vote_count", nullable = false)
    private int voteCount;         // 이 보기의 투표 수

    public QnAPollOption(QnAPoll poll, String label, String content) {
        this.poll = poll;
        this.label = label;
        this.content = content;
        this.voteCount = 0;
    }

    public void setPoll(QnAPoll poll) {
        this.poll = poll;
    }

    // ✅ 득표수 +1
    public void increaseVoteCount() {
        this.voteCount++;
    }
}
