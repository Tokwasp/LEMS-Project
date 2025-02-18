package lems.cowshed.api.controller.dto.bookmark.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Schema(description = "북마크 모임 반환")
public class BookmarkResponseDto {

    @Schema(description = "북마크 모임 리스트")
    List<EventPreviewResponseDto> bookmarks;

    public BookmarkResponseDto(List<EventPreviewResponseDto> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public static BookmarkResponseDto create(List<EventPreviewResponseDto> bookmarks){
        return new BookmarkResponseDto(bookmarks);
    }

    public static BookmarkResponseDto from(List<Bookmark> bookmarks){
//        List<EventPreviewResponseDto> results = bookmarks.stream()
//                .map((Bookmark bookmark) -> new EventPreviewResponseDto(null))
//                .collect(Collectors.toList());
        return null;
    }
}

