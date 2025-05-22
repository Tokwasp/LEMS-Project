package lems.cowshed.api.controller.comment;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.comment.request.CommentModifyRequest;
import lems.cowshed.api.controller.dto.comment.request.CommentSaveRequest;
import lems.cowshed.api.controller.dto.comment.response.CommentInfo;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
public class CommentController implements CommentSpecification {

    private final CommentService commentService;

    @GetMapping("/posts/{post-id}/comments")
    public CommonResponse<CommentInfo> getPostComments(@PathVariable("post-id") Long postId) {
        commentService.getPostComments(postId, LocalDateTime.now());
        return null;
    }

    @PostMapping("/posts/{post-id}/comments")
    public CommonResponse<Void> save(@RequestBody CommentSaveRequest request, @PathVariable("post-id") Long postId,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.save(request, postId, userDetails.getUserId());
        return CommonResponse.success();
    }

    @PutMapping("/comments/{comment-id}")
    public CommonResponse<Void> modify(@RequestBody CommentModifyRequest request, @PathVariable("comment-id") Long commentId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.modify(request, commentId, userDetails.getUserId());
        return CommonResponse.success();
    }

    @DeleteMapping("/comment/{comment-id}")
    public CommonResponse<Void> delete(@PathVariable("comment-id") Long commentId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.delete(commentId, userDetails.getUserId());
        return CommonResponse.success();
    }
}
