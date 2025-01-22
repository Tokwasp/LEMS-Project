package lems.cowshed.api.controller.dto.bookmark.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "북마크 모임 반환")
public class BookmarkResponseDto {

    @NotEmpty
    @Schema(description = "북마크 모임 리스트")
    List<EventPreviewResponseDto> bookmarks;

    @Builder
    private BookmarkResponseDto(List<EventPreviewResponseDto> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public static BookmarkResponseDto of(List<EventPreviewResponseDto> bookmarks){
        return BookmarkResponseDto.builder()
                .bookmarks(bookmarks)
                .build();
    }
}

