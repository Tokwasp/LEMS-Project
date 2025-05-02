package lems.cowshed.api.controller.bookmark;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.service.BookmarkService;
import lems.cowshed.domain.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkApiController implements BookmarkSpecification{

    private final BookmarkService bookmarkService;

    @PostMapping("/{event-id}")
    public CommonResponse<Void> saveBookmark(@PathVariable("event-id") Long eventId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        bookmarkService.saveBookmark(eventId, userDetails.getUserId());
        return CommonResponse.success();
    }

    @PatchMapping("/{event-id}")
    public CommonResponse<Void> deleteBookmark(@PathVariable("event-id") long eventId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        bookmarkService.deleteBookmark(eventId, userDetails.getUserId());
        return CommonResponse.success();
    }
}
