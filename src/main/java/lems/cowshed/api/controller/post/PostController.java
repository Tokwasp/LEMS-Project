package lems.cowshed.api.controller.post;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.dto.post.request.PostModifyRequest;
import lems.cowshed.dto.post.request.PostSaveRequest;
import lems.cowshed.dto.post.response.PostPagingInfo;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.dto.post.response.PostSimpleInfo;
import lems.cowshed.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostController implements PostSpecification {

    private final PostService postService;

    @GetMapping("/posts/{post-id}")
    public CommonResponse<PostSimpleInfo> get(@PathVariable("post-id") Long postId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostSimpleInfo postSimpleInfo = postService.get(postId, userDetails.getUserId());
        return CommonResponse.success(postSimpleInfo);
    }

    @PostMapping("/events/{event-id}/posts")
    public CommonResponse<Void> save(@RequestBody PostSaveRequest request, @PathVariable("event-id") Long eventId,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.save(request, eventId, userDetails.getUserId());
        return CommonResponse.success();
    }

    @PutMapping("/posts/{post-id}")
    public CommonResponse<Void> modify(@RequestBody PostModifyRequest request, @PathVariable("post-id") Long postId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.modify(request, userDetails.getUserId(), postId);
        return CommonResponse.success();
    }

    @DeleteMapping("/posts/{post-id}")
    public CommonResponse<Void> delete(@PathVariable("post-id") Long postId, CustomUserDetails userDetails) {
        postService.delete(postId, userDetails.getUserId());
        return CommonResponse.success();
    }

    @GetMapping("/events/{event-id}/posts/search")
    public CommonResponse<PostPagingInfo> getPaging(@PathVariable("event-id") Long eventId,
                                                    @PageableDefault(page = 0, size = 5) Pageable pageable,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostPagingInfo postPagingInfo = postService.getPaging(eventId, pageable, userDetails.getUserId());
        return CommonResponse.success(postPagingInfo);
    }
}
