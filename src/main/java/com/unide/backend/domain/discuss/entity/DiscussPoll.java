package com.unide.backend.domain.discuss.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "dis_poll")
public class DiscussPoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poll_id")
    private Long id;

    // 게시글 id
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "title", length = 100, nullable = false)
    private String title;          // 질문 텍스트로 사용

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "is_private", nullable = false)
    private boolean privatePoll;

    @Column(name = "allows_multi", nullable = false)
    private boolean allowsMulti;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscussPollOption> options = new ArrayList<>();

    // ✅ 전체 투표 수
    @Column(name = "total_votes", nullable = false)
    private int totalVotes = 0;

    // === 연관관계 편의 메서드 ===
    public void addOption(DiscussPollOption option) {
        options.add(option);
        option.setPoll(this);
    }

    protected DiscussPoll() {}

    public DiscussPoll(Long postId,
                       Long authorId,
                       String title,
                       LocalDateTime endTime,
                       boolean privatePoll,
                       boolean allowsMulti) {
        this.postId = postId;
        this.authorId = authorId;
        this.title = title;
        this.endTime = endTime;
        this.privatePoll = privatePoll;
        this.allowsMulti = allowsMulti;
        this.createdAt = LocalDateTime.now();
        this.totalVotes = 0;
    }

    // ✅ 전체 투표 수 +1
    public void increaseTotalVotes() {
        this.totalVotes++;
    }

    // ===== Getter =====
    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public Long getAuthorId() { return authorId; }
    public String getTitle() { return title; }
    public String getQuestion() { return title; }
    public LocalDateTime getEndTime() { return endTime; }
    public boolean isPrivatePoll() { return privatePoll; }
    public boolean isAllowsMulti() { return allowsMulti; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<DiscussPollOption> getOptions() { return options; }
    public int getTotalVotes() { return totalVotes; }
}
