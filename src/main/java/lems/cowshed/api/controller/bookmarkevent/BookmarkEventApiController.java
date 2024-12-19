package lems.cowshed.api.controller.bookmarkevent;

import lems.cowshed.api.controller.dto.CommonResponse;
import lems.cowshed.api.controller.dto.bookmarkevent.request.BookmarkEventRequestDto;
import lems.cowshed.api.controller.dto.bookmarkevent.response.BookmarkEventResponseDto;
import lems.cowshed.api.controller.dto.user.response.BookmarkMyPageResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
