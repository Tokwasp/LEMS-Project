package lems.cowshed.api.controller.bookmark;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkApiController implements BookmarkSpecification{

    private final BookmarkService bookmarkService;

    @GetMapping
    public CommonResponse<BookmarkResponseDto> getPageBookmarks(Pageable pageable) {
        return null;
    }

    @PostMapping("/{event-id}")
    public CommonResponse<Void> saveBookmark(@PathVariable("event-id") Long eventId) {
        bookmarkService.saveBookmarkEvent(eventId);
        return CommonResponse.success();
    }

//    @PostMapping
//    public CommonResponse<Void> createBookmark(@RequestBody @Valid BookmarkSaveRequestDto request) {
//        bookmarkService.createBookmark(SecurityContextUtil.getUserId(), request);
//        return CommonResponse.success();
//    }
//
//    @PutMapping("/{bookmarkId}")
//    public CommonResponse<Void> editBookmarkName(@RequestBody @Valid BookmarkEditRequestDto request, @PathVariable long bookmarkId) {
//        bookmarkService.editBookmarkName(request, bookmarkId);
//        return CommonResponse.success();
//    }

    @DeleteMapping("/{bookmarkId}")
    public CommonResponse<Void> deleteBookmark(@PathVariable long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return CommonResponse.success();
    }
}
