package com.unide.backend.domain.discuss.controller;

import com.unide.backend.domain.discuss.dto.*;
import com.unide.backend.domain.discuss.service.DiscussAttachService;
import com.unide.backend.domain.discuss.service.DiscussLikeService;
import com.unide.backend.domain.discuss.service.DiscussPollService;
import com.unide.backend.domain.discuss.service.DiscussService;
import com.unide.backend.domain.user.entity.User;
import com.unide.backend.global.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DiscussController {

    private final DiscussService discussService;
    private final DiscussLikeService discussLikeService;
    private final DiscussAttachService discussAttachService;
    private final DiscussPollService discussPollService;

    // 🔹 게시글 목록 조회
    //    GET /api/dis_board_list/{pageNumber}?pageSize=10
    @GetMapping("/dis_board_list/{pageNumber}")
    public DiscussListResponse getDiscussList(
            @PathVariable int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return discussService.getDiscussList(pageNumber, pageSize);
    }

    // 🔹 게시글 단건 조회
    //    GET /api/dis_board/{postId}
    @GetMapping("/dis_board/{postId}")
    public DiscussDetailResponse getDiscuss(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        User viewer = principalDetails != null ? principalDetails.getUser() : null;
        return discussService.getDiscussDetail(postId, viewer);
    }

    // 🔹 게시글 작성
    //    POST /api/dis_board
    @PostMapping("/dis_board")
    public DiscussCreateResponse createDiscuss(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody DiscussCreateRequest request
    ) {
        User author = principalDetails.getUser();
        return discussService.createDiscuss(author, request);
    }

    // 🔹 게시글 수정
    //    PUT /api/dis_board/{postId}
    @PutMapping("/dis_board/{postId}")
    public DiscussUpdateResponse updateDiscuss(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody DiscussUpdateRequest request
    ) {
        User editor = principalDetails.getUser();
        return discussService.updateDiscuss(postId, editor, request);
    }

    // 🔹 게시글 삭제
    //    DELETE /api/dis_board/{postId}
    @DeleteMapping("/dis_board/{postId}")
    public DiscussDeleteResponse deleteDiscuss(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        User requester = principalDetails.getUser();
        return discussService.deleteDiscuss(postId, requester);
    }

    // 🔹 게시글 좋아요 토글
    //    POST /api/dis_board/{postId}/like
    @PostMapping("/dis_board/{postId}/like")
    public PostLikeResponse likeDiscuss(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        User liker = principalDetails.getUser();
        return discussLikeService.toggleLike(postId, liker);
    }

    // 🔹 첨부파일 등록
    //    POST /api/dis_board/{postId}/attach
    @PostMapping("/dis_board/{postId}/attach")
    public AttachResponse addAttachment(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody AttachRequest request
    ) {
        User user = principalDetails.getUser();
        request.setPostId(postId);  // path variable 과 body 동기화
        return discussAttachService.addAttachment(request, user);
    }

    // 🔹 투표 생성
    //    POST /api/dis_board/{postId}/poll
    @PostMapping("/dis_board/{postId}/poll")
    public PollCreateResponse createPoll(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody PollCreateRequest request
    ) {
        User user = principalDetails.getUser();
        request.setPostId(postId);
        return discussPollService.createPoll(request, user);
    }
}
