package lems.cowshed.api.controller.bookmarkevent;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.bookmarkevent.request.BookmarkEventRequestDto;
import lems.cowshed.api.controller.dto.bookmarkevent.response.BookmarkEventResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookmarkEvent")
public class BookmarkEventApiController implements BookmarkEventSpecification {

    @GetMapping
    public CommonResponse<BookmarkEventResponseDto> findAllBookmarkEvents(@PathVariable Long bookmarkId) {
        return null;
    }

    @PostMapping
    public CommonResponse<Void> saveBookmarkEvent(@PathVariable Long bookmarkId, @RequestBody BookmarkEventRequestDto requestDto) {
        return null;
    }

    @DeleteMapping
    public CommonResponse<Void> deleteBookmarkEvent(@PathVariable Long bookmarkId, @RequestBody BookmarkEventRequestDto requestDto) {
        return null;
    }


}
