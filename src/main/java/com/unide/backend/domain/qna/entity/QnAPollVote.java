package com.unide.backend.domain.qna.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qnas_poll_vote")
public class QnAPollVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private QnAPoll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private QnAPollOption option;

    @Column(name = "voter_id", nullable = false)
    private Long voterId;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;

    protected QnAPollVote() {}

    public QnAPollVote(QnAPoll poll, QnAPollOption option, Long voterId) {
        this.poll = poll;
        this.option = option;
        this.voterId = voterId;
        this.votedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public QnAPoll getPoll() { return poll; }
    public QnAPollOption getOption() { return option; }
    public Long getVoterId() { return voterId; }
    public LocalDateTime getVotedAt() { return votedAt; }
}
