package com.unide.backend.domain.qna.controller;

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

import com.unide.backend.domain.qna.dto.QnADto;
import com.unide.backend.domain.qna.dto.QnAPollCreateRequest;
import com.unide.backend.domain.qna.dto.QnAPollResponse;
import com.unide.backend.domain.qna.dto.QnAPollVoteRequest;
import com.unide.backend.domain.qna.dto.QnAPollVoteResponse;
import com.unide.backend.domain.qna.service.QnAPollService;
import com.unide.backend.domain.qna.service.QnAService;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna_board")
public class QnAController {

    private final QnAService qnaService;
    private final QnAPollService qnAPollService;



    // ===== QnA 목록 =====
    @GetMapping
    public List<QnADto> list(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum
    ) {
        return qnaService.getQnAList(pageNum);
    }

    @GetMapping("/list")
    public List<QnADto> listAll(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum
    ) {
        return qnaService.getQnAList(pageNum);
    }

    // ===== QnA 상세 =====
    @GetMapping("/{postId}")
    public QnADto detail(@PathVariable("postId") Long postId) {
        return qnaService.getQnA(postId);
    }

    // ===== QnA 작성 =====
    @PostMapping
    public QnADto create(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody QnADto qnaDto
    ) {
        Long authorId = userDetails.getUser().getId();
        return qnaService.createQnA(qnaDto, authorId);
    }

    // ===== QnA 수정 =====
    @PutMapping("/{postId}")
    public QnADto update(
            @PathVariable("postId") Long postId,
            @RequestBody QnADto qnaDto
    ) {
        return qnaService.updateQnA(postId, qnaDto);
    }

    // ===== QnA 삭제 =====
    @DeleteMapping("/{postId}")
    public void delete(@PathVariable("postId") Long postId) {
        qnaService.deleteQnA(postId);
    }

    // ===== QnA 검색 =====
    @GetMapping("/search")
    public List<QnADto> search(@RequestParam("keyword") String keyword) {
        return qnaService.searchQnAs(keyword);
    }

    // ===== 첨부파일 =====
    @PostMapping("/{postId}/attach")
    public Map<String, Object> attachFile(
            @PathVariable Long postId,
            @RequestBody Map<String, String> request
    ) {
        String fileUrl = request.get("contents");
        return qnaService.attachFile(postId, fileUrl);
    }

    // ===== QnA 좋아요 토글 =====
    @PostMapping("/{postId}/like")
    public QnADto toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return qnaService.toggleLike(postId, userId);
    }

    // ===== 투표 생성 =====
    // POST /api/qna_board/{postId}/poll
    @PostMapping("/{postId}/poll")
    public ResponseEntity<QnAPollResponse> createPoll(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody QnAPollCreateRequest request
    ) {
        Long authorId = userDetails.getUser().getId();

        // body 안의 post_id 를 pathVariable 과 맞춰주기 (실수 방지용)
        request.setPost_id(postId);

        QnAPollResponse response = qnAPollService.createPoll(postId, authorId, request);
        return ResponseEntity.ok(response);
    }

    // ===== 투표 하기 =====
    // POST /api/qna_board/{postId}/poll/{pollId}/vote
    @PostMapping("/{postId}/poll/{pollId}/vote")
public ResponseEntity<QnAPollVoteResponse> vote(
        @PathVariable("postId") Long postId,          // 안 쓰더라도 받아야 함
        @PathVariable("pollId") Long pollId,
        @AuthenticationPrincipal PrincipalDetails userDetails,
        @RequestBody QnAPollVoteRequest request
) {
    Long voterId = userDetails.getUser().getId();

    QnAPollVoteResponse response =
            qnAPollService.vote(voterId, pollId, request);

    return ResponseEntity.ok(response);
}
@GetMapping("/{postId}/poll")
public ResponseEntity<QnAPollResponse> getPollByPost(
        @PathVariable Long postId,
        @AuthenticationPrincipal PrincipalDetails userDetails
) {
    Long userId = userDetails != null ? userDetails.getUser().getId() : null;

    QnAPollResponse response = qnAPollService.getPollByPostId(postId, userId);
    return ResponseEntity.ok(response);
}


}
