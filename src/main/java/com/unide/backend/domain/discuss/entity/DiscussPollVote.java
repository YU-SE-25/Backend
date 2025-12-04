package com.unide.backend.domain.discuss.entity;

import java.time.LocalDateTime;

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
@Table(name = "dis_poll_vote")
public class DiscussPollVote {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private DiscussPoll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private DiscussPollOption option;

    @Column(name = "voter_id", nullable = false)
    private Long voterId;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;

    
    protected DiscussPollVote() {}

    public DiscussPollVote(DiscussPoll poll, DiscussPollOption option, Long voterId) {
        this.poll = poll;
        this.option = option;
        this.voterId = voterId;
        this.votedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public DiscussPoll getPoll() { return poll; }
    public DiscussPollOption getOption() { return option; }
    public Long getVoterId() { return voterId; }
    public LocalDateTime getVotedAt() { return votedAt; }
}
