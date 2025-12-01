package com.unide.backend.domain.studygroup.discuss.controller;

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

import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussDto;
import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussPollCreateRequest;
import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussPollResponse;
import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussPollVoteRequest;
import com.unide.backend.domain.studygroup.discuss.dto.st_DiscussPollVoteResponse;
import com.unide.backend.domain.studygroup.discuss.service.st_DiscussPollService;
import com.unide.backend.domain.studygroup.discuss.service.st_DiscussService;
import com.unide.backend.global.dto.PageResponse;
import com.unide.backend.global.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/studygroup/discuss")
public class st_DiscussController {

    private final st_DiscussService discussService;
    private final st_DiscussPollService pollService;

    // ========= 목록 조회 =========
    // GET /api/studygroup/discuss/{groupId}?page=1
    @GetMapping("/{groupId}")
        public ResponseEntity<PageResponse<st_DiscussDto>> listGroupDiscuss(
        @PathVariable Long groupId,
        @RequestParam(name = "page", defaultValue = "1") int page
) {PageResponse<st_DiscussDto> response =
        discussService.getDiscussList(groupId, page);

    return ResponseEntity.ok(response);
}


    // ========= 단건 조회 =========
    // GET /api/studygroup/discuss/{groupId}/{postId}
    @GetMapping("/{groupId}/{postId}")
    public st_DiscussDto detail(
            @PathVariable Long groupId,
            @PathVariable Long postId
    ) {
        return discussService.getDiscuss(groupId, postId);
    }

    // ========= 작성 =========
    // POST /api/studygroup/discuss/{groupId}
    @PostMapping("/{groupId}")
    public st_DiscussDto create(
            @PathVariable Long groupId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody st_DiscussDto dto
    ) {
        Long authorId = userDetails.getUser().getId();
        return discussService.createDiscuss(groupId, dto, authorId);
    }

    // ========= 수정 =========
    // PUT /api/studygroup/discuss/{groupId}/{postId}
    @PutMapping("/{groupId}/{postId}")
    public st_DiscussDto update(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @RequestBody st_DiscussDto dto
    ) {
        return discussService.updateDiscuss(groupId, postId, dto);
    }

    // ========= 삭제 =========
    // DELETE /api/studygroup/discuss/{groupId}/{postId}
    @DeleteMapping("/{groupId}/{postId}")
    public void delete(
            @PathVariable Long groupId,
            @PathVariable Long postId
    ) {
        discussService.deleteDiscuss(groupId, postId);
    }

    // ========= 검색 =========
    // GET /api/studygroup/discuss/{groupId}/search?keyword=...
    @GetMapping("/{groupId}/search")
    public List<st_DiscussDto> search(
            @PathVariable Long groupId,
            @RequestParam("keyword") String keyword
    ) {
        return discussService.searchDiscusses(groupId, keyword);
    }

    // ========= 첨부파일 등록 =========
    // POST /api/studygroup/discuss/{groupId}/{postId}/attach
    @PostMapping("/{groupId}/{postId}/attach")
    public Map<String, Object> attachFile(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @RequestBody Map<String, String> request
    ) {
        String fileUrl = request.get("contents");
        return discussService.attachFile(groupId, postId, fileUrl);
    }

    // ========= 게시글 좋아요 토글 =========
    // POST /api/studygroup/discuss/{groupId}/{postId}/like
    @PostMapping("/{groupId}/{postId}/like")
    public st_DiscussDto toggleLike(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return discussService.toggleLike(groupId, postId, userId);
    }

    // ========= 투표 생성 =========
    // POST /api/studygroup/discuss/{groupId}/{postId}/poll
    @PostMapping("/{groupId}/{postId}/poll")
    public ResponseEntity<st_DiscussPollResponse> createPoll(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody st_DiscussPollCreateRequest request
    ) {
        Long authorId = userDetails.getUser().getId();

        // body 안의 post_id 를 path 변수와 동기화
        request.setPost_id(postId);

        st_DiscussPollResponse response =
                pollService.createPoll(groupId, postId, authorId, request);

        return ResponseEntity.ok(response);
    }

    // ========= 투표 하기 =========
    // POST /api/studygroup/discuss/{groupId}/poll/{pollId}/vote
    @PostMapping("/{groupId}/poll/{pollId}/vote")
    public ResponseEntity<st_DiscussPollVoteResponse> vote(
            @PathVariable Long groupId,   // 경로 맞추기용, 서비스에서는 안 씀
            @PathVariable Long pollId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody st_DiscussPollVoteRequest request
    ) {
        Long voterId = userDetails.getUser().getId();

        st_DiscussPollVoteResponse response =
                pollService.vote(voterId, pollId, request);

        return ResponseEntity.ok(response);
    }

    // ========= 투표 조회 =========
    // GET /api/studygroup/discuss/{groupId}/{postId}/poll
    @GetMapping("/{groupId}/{postId}/poll")
    public ResponseEntity<st_DiscussPollResponse> getPollByPost(
            @PathVariable Long groupId,
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        Long userId = (userDetails != null) ? userDetails.getUser().getId() : null;

        st_DiscussPollResponse response =
                pollService.getPollByPostId(postId, userId);

        return ResponseEntity.ok(response);
    }
}
