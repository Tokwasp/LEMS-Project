package lems.cowshed.api.controller.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.api.controller.dto.comment.request.CommentModifyRequest;
import lems.cowshed.api.controller.dto.comment.request.CommentSaveRequest;
import lems.cowshed.api.controller.dto.comment.response.CommentInfo;
import lems.cowshed.config.swagger.ApiErrorCodeExamples;
import lems.cowshed.domain.user.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "comment api" , description = "댓글 API")
public interface CommentSpecification {

    @Operation(summary = "게시글 댓글 조회" , description = "게시글의 댓글을 조회 합니다.")
    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<CommentInfo> getPostComments(@PathVariable Long postId);

    @Operation(summary = "댓글 등록" , description = "댓글을 등록 합니다.")
    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<Void> save(@RequestBody CommentSaveRequest request, @PathVariable Long postId,
                              @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "댓글 수정", description = "댓글을 수정 합니다.")
    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<Void> modify(@RequestBody CommentModifyRequest request, @PathVariable Long commentId,
                                @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제 합니다.")
    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<Void> delete(@PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails);
}
