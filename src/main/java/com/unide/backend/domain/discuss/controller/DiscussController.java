package com.unide.backend.domain.discuss.controller;

import java.util.HashMap;
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
import com.unide.backend.domain.discuss.dto.DiscussReportCreateRequestDto;
import com.unide.backend.domain.discuss.service.DiscussPollService;
import com.unide.backend.domain.discuss.service.DiscussReportService;
import com.unide.backend.domain.discuss.service.DiscussService;
import com.unide.backend.global.dto.PageResponse;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dis_board")
public class DiscussController {

    private final DiscussService discussService;
    private final DiscussPollService discussPollService;   // ✅ 투표 서비스 추가
    private final DiscussReportService discussReportService;   // ✅ 신고 서비스 추가

     // ===== 목록 조회 =====
    @GetMapping
    public ResponseEntity<PageResponse<DiscussDto>> listDiscuss(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long viewerId = (userDetails != null) ? userDetails.getUser().getId() : null;
        PageResponse<DiscussDto> response = discussService.getDiscussList(page, viewerId);
        return ResponseEntity.ok(response);
    }

    // ===== 상세 조회 =====
    @GetMapping("/{postId}")
    public DiscussDto detail(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long viewerId = (userDetails != null) ? userDetails.getUser().getId() : null;
        return discussService.getDiscuss(postId, viewerId);
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
// 🔹 게시글 신고하기
   // POST /api/dis_board/{postId}/reports
@PostMapping("/{postId}/reports")
public ResponseEntity<Map<String, Object>> reportPost(
        @PathVariable("postId") Long postId,
        @AuthenticationPrincipal PrincipalDetails userDetails,
        @RequestBody DiscussReportCreateRequestDto request
) {
    Long reporterId = userDetails.getUser().getId();

    // 신고 저장
    discussReportService.reportPost(postId, reporterId, request);

    // 성공 메시지 반환
    Map<String, Object> response = new HashMap<>();
    response.put("message", "신고가 접수되었습니다.");

    return ResponseEntity.ok(response);
}

}
