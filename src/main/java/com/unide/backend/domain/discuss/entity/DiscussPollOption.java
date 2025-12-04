package com.unide.backend.domain.discuss.entity;

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
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dis_poll_option")
public class DiscussPollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private DiscussPoll poll;

    @Column(name = "content", nullable = false, length = 200)
    private String content;

    // ✅ 옵션별 득표 수
    @Column(name = "vote_count", nullable = false)
    private int voteCount = 0;

    @Column(name = "label", nullable = false)   // "1", "2", "3" ...
    private String label;

    protected DiscussPollOption() {}

    public DiscussPollOption(DiscussPoll poll, String label, String content) {
        this.poll = poll;
        this.label = label;
        this.content = content;
        this.voteCount = 0;
    }

    public void setPoll(DiscussPoll poll) {
        this.poll = poll;
    }

    // ✅ 득표 수 +1
    public void increaseVoteCount() {
        this.voteCount++;
    }
}
