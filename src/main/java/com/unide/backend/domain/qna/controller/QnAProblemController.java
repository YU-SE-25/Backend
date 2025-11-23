package com.unide.backend.domain.qna.controller;

import com.unide.backend.domain.problems.entity.Problems;
import com.unide.backend.domain.qna.dto.AddProblemRequest;
import com.unide.backend.domain.qna.dto.QnAProblemDto;
import com.unide.backend.domain.qna.service.QnAProblemPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Q&A 게시글과 문제 정보를 연동 / 해제 / 조회하는 API
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna_board/problem")
public class QnAProblemController {

    private final QnAProblemPostService qnaProblemPostService;

    /**
     * 게시글에 문제를 연동한다.
     * body 예시: { "postId": 1, "problemId": 3 }
     */
    @PostMapping("/link")
    public ResponseEntity<Void> linkProblemToPost(@RequestBody AddProblemRequest request) {
        qnaProblemPostService.linkProblemToPost(request.getPostId(), request.getProblemId());
        return ResponseEntity.ok().build();
    }

    /**
     * 게시글과 문제의 연동을 해제한다.
     */
    @DeleteMapping("/unlink/{postId}")
    public ResponseEntity<Void> unlinkProblemFromPost(@PathVariable Long postId) {
        qnaProblemPostService.unlinkProblemFromPost(postId);
        return ResponseEntity.ok().build();
    }

    /**
     * 게시글에 연동된 문제를 조회한다.
     * - 연동된 문제가 있으면 QnAProblemDto 반환 (200)
     * - 없으면 204 No Content
     */
    @GetMapping("/{postId}")
    public ResponseEntity<QnAProblemDto> getLinkedProblem(@PathVariable Long postId) {
        Optional<Problems> linked = qnaProblemPostService.getLinkedProblem(postId);

        return linked
                .map(problem -> ResponseEntity.ok(QnAProblemDto.fromEntity(problem)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
