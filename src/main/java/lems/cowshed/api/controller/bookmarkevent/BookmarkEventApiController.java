package lems.cowshed.api.controller.bookmarkevent;

import lems.cowshed.api.controller.dto.CommonResponse;
import lems.cowshed.api.controller.dto.bookmarkevent.request.BookmarkEventRequestDto;
import lems.cowshed.api.controller.dto.user.response.BookmarkMyPageResponseDto;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmark/{bookmarkId}")
public class BookmarkEventApiController implements BookmarkEventSpecification {

    @GetMapping
    public CommonResponse<List<BookmarkMyPageResponseDto>> findAllBookmarks(@PathVariable Long bookmarkId) {
        return null;
    }

    @PostMapping
    public CommonResponse<Void> saveBookmark(@PathVariable Long bookmarkId, @RequestBody BookmarkEventRequestDto requestDto) {
        return null;
    }

    @DeleteMapping
    public CommonResponse<Void> deleteBookmark(@PathVariable Long bookmarkId, @RequestBody BookmarkEventRequestDto requestDto) {
        return null;
    }


}
