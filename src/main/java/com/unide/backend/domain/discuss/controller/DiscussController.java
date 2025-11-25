package com.unide.backend.domain.discuss.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unide.backend.domain.discuss.dto.DiscussDto;
import com.unide.backend.domain.discuss.dto.DiscussPollCreateRequest;
import com.unide.backend.domain.discuss.dto.DiscussPollResponse;
import com.unide.backend.domain.discuss.dto.DiscussPollVoteRequest;
import com.unide.backend.domain.discuss.dto.DiscussPollVoteResponse;
import com.unide.backend.domain.discuss.service.DiscussPollService;
import com.unide.backend.domain.discuss.service.DiscussService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dis_board")
public class DiscussController {

    private final DiscussService discussService;
    private final DiscussPollService discussPollService;   // ✅ 투표 서비스 추가

    // 목록
    @GetMapping
    public List<DiscussDto> list(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum
    ) {
        return discussService.getDiscussList(pageNum);
    }

    @GetMapping("/list")
    public List<DiscussDto> listAll(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum
    ) {
        return discussService.getDiscussList(pageNum);
    }

    // 상세
    @GetMapping("/{postId}")
    public DiscussDto detail(@PathVariable("postId") Long postId) {
        return discussService.getDiscuss(postId);
    }

    // 작성
    @PostMapping
    public DiscussDto create(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody DiscussDto discussDto
    ) {
        Long authorId = userDetails.getUser().getId();
        return discussService.createDiscuss(discussDto, authorId);
    }

    // 수정
    @PutMapping("/{postId}")
    public DiscussDto update(
            @PathVariable("postId") Long postId,
            @RequestBody DiscussDto discussDto
    ) {
        return discussService.updateDiscuss(postId, discussDto);
    }

    // 삭제
    @DeleteMapping("/{postId}")
    public void delete(@PathVariable("postId") Long postId) {
        discussService.deleteDiscuss(postId);
    }

    // 검색
    @GetMapping("/search")
    public List<DiscussDto> search(@RequestParam("keyword") String keyword) {
        return discussService.searchDiscusses(keyword);
    }

    // 첨부파일 첨가
    @PostMapping("/{postId}/attach")
    public Map<String, Object> attachFile(
            @PathVariable Long postId,
            @RequestBody Map<String, String> request
    ) {
        String fileUrl = request.get("contents");   // 문서에 맞춰 contents 로 받음
        return discussService.attachFile(postId, fileUrl);
    }

    // ===== discuss 게시글 좋아요 토글 =====
    @PostMapping("/{postId}/like")
    public DiscussDto toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return discussService.toggleLike(postId, userId);
    }

    // ===== 투표 생성 =====
    // POST /api/dis_board/{postId}/poll
    @PostMapping("/{postId}/poll")
    public ResponseEntity<DiscussPollResponse> createPoll(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody DiscussPollCreateRequest request
    ) {
        Long authorId = userDetails.getUser().getId();

        // body 안의 post_id 를 pathVariable 과 맞춰주기 (실수 방지용)
        request.setPost_id(postId);

        DiscussPollResponse response = discussPollService.createPoll(postId, authorId, request);
        return ResponseEntity.ok(response);
    }

    // ===== 투표 하기 =====
    // POST /api/dis_board/{postId}/poll/{pollId}/vote
    @PostMapping("/{postId}/poll/{pollId}/vote")
    public ResponseEntity<DiscussPollVoteResponse> vote(
            @PathVariable("postId") Long postId,          // 경로 맞추기용
            @PathVariable("pollId") Long pollId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody DiscussPollVoteRequest request
    ) {
        Long voterId = userDetails.getUser().getId();

        DiscussPollVoteResponse response =
                discussPollService.vote(voterId, pollId, request);

        return ResponseEntity.ok(response);
    }

    // ===== 투표 조회 =====
    // GET /api/dis_board/{postId}/poll
    @GetMapping("/{postId}/poll")
    public ResponseEntity<DiscussPollResponse> getPollByPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long userId = (userDetails != null) ? userDetails.getUser().getId() : null;

        DiscussPollResponse response = discussPollService.getPollByPostId(postId, userId);
        return ResponseEntity.ok(response);
    }
}
