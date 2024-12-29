package lems.cowshed.api.controller.bookmark;

import jakarta.validation.Valid;
import lems.cowshed.api.controller.SecurityContextUtil;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkEditRequestDto;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkSaveRequestDto;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookmarkApiController implements BookmarkSpecification{

    private final BookmarkService bookmarkService;

    @GetMapping
    public CommonResponse<BookmarkResponseDto> getAllBookmarks() {
        return CommonResponse.success(bookmarkService.getAllBookmarks(SecurityContextUtil.getUserId()));
    }

    @PostMapping
    public CommonResponse<Void> createBookmark(@RequestBody @Valid BookmarkSaveRequestDto request) {
        bookmarkService.createBookmark(SecurityContextUtil.getUserId(), request);
        return CommonResponse.success();
    }

    @PutMapping("/{bookmarkId}")
    public CommonResponse<Void> editBookmarkName(@RequestBody @Valid BookmarkEditRequestDto request, @PathVariable long bookmarkId) {
        bookmarkService.editBookmarkName(request, bookmarkId);
        return CommonResponse.success();
    }

    @DeleteMapping("/{bookmarkId}")
    public CommonResponse<Void> deleteBookmark(@PathVariable long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return CommonResponse.success();
    }
}
