package lems.cowshed.api.controller.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.dto.post.request.PostModifyRequest;
import lems.cowshed.dto.post.request.PostSaveRequest;
import lems.cowshed.dto.post.response.PostPagingInfo;
import lems.cowshed.config.swagger.ApiErrorCodeExamples;
import lems.cowshed.domain.user.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "post api" , description = "게시글 API")
public interface PostSpecification {

    @Operation(summary = "게시글 조회", description = "게시글을 페이징 조회 합니다.")
    CommonResponse<PostPagingInfo> getPaging(@PageableDefault(page = 0, size = 5) Pageable pageable,
                                             @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "게시글 등록" , description = "요청 정보를 통해 게시글을 등록 합니다.")
    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<Void> save(@RequestBody PostSaveRequest postSaveRequest, @PathVariable Long eventId,
                              @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "게시글 수정", description = "해당 게시글의 내용을 수정 합니다.")
    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<Void> modify(@RequestBody PostModifyRequest postModifyRequest, @PathVariable Long postId,
                                @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "게시글 삭제", description = "해당 게시글을 삭제 합니다.")
    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<Void> delete(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails);
}
