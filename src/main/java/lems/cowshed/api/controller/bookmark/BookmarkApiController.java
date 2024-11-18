package lems.cowshed.api.controller.bookmark;

import lems.cowshed.api.controller.dto.CommonResponse;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkDeleteRequestDto;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkEditRequestDto;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkSaveRequestDto;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmark")
public class BookmarkApiController implements BookmarkSpecification{

    @GetMapping
    public CommonResponse<BookmarkResponseDto> findAllBookmarks() {
        return null;
    }

    @PostMapping
    public CommonResponse<Void> saveBookmark(@RequestBody BookmarkSaveRequestDto requestDto) {
        return null;
    }

    @PutMapping
    public CommonResponse<Void> editBookmark(@RequestBody BookmarkEditRequestDto requestDto) {
        return null;
    }

    @DeleteMapping
    public CommonResponse<Void> deleteBookmark(@RequestBody BookmarkDeleteRequestDto requestDto) {
        return null;
    }
}
