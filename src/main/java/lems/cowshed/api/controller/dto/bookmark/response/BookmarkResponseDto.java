package lems.cowshed.api.controller.dto.bookmark.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Schema(description = "북마크 모임 반환")
public class BookmarkResponseDto {

    @Schema(description = "북마크 모임 리스트")
    List<EventPreviewResponseDto> bookmarks;

    boolean isLast;

    @Builder
    public BookmarkResponseDto(List<EventPreviewResponseDto> bookmarks, boolean isLast) {
        this.bookmarks = bookmarks;
        this.isLast = isLast;
    }

    public static BookmarkResponseDto of(List<EventPreviewResponseDto> bookmarks, boolean isLast){
        return BookmarkResponseDto.builder()
                .bookmarks(bookmarks)
                .isLast(isLast)
                .build();
    }

}