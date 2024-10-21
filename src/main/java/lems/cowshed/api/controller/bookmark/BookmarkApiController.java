package lems.cowshed.api.controller.bookmark;

import lems.cowshed.api.controller.dto.CommonResponse;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkEditRequestDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmark")
public class BookmarkApiController implements BookmarkSpecification{

    @GetMapping
    public CommonResponse<List<String>> findAllBookmarkFolders() {
        return null;
    }

    @PostMapping
    public CommonResponse<Void> saveBookmarkFolder(@RequestBody String folderName) {
        return null;
    }

    @PutMapping
    public CommonResponse<Void> editBookmarkFolder(@RequestBody BookmarkEditRequestDto requestDto) {
        return null;
    }

    @DeleteMapping
    public CommonResponse<Void> deleteBookmarkFolder(@RequestBody Long bookmarkId) {
        return null;
    }


}
