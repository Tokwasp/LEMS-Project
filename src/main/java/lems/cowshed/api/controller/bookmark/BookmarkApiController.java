package lems.cowshed.api.controller.bookmark;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.service.BookmarkService;
import lems.cowshed.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkApiController implements BookmarkSpecification{

    private final BookmarkService bookmarkService;

    @GetMapping
    public CommonResponse<BookmarkResponseDto> getAllBookmarks(@AuthenticationPrincipal CustomUserDetails userDetails){
        BookmarkResponseDto response = bookmarkService.getAllBookmarks(userDetails.getUserId());
        return CommonResponse.success(response);
    }

    @PostMapping("/{event-id}")
    public CommonResponse<Void> saveBookmark(@PathVariable("event-id") Long eventId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        bookmarkService.saveBookmark(eventId, userDetails.getUserId());
        return CommonResponse.success();
    }

    @DeleteMapping("/{bookmarkId}")
    public CommonResponse<Void> deleteBookmark(@PathVariable long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return CommonResponse.success();
    }
}
