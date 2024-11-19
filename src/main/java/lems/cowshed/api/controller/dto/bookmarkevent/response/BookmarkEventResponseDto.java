package lems.cowshed.api.controller.dto.bookmarkevent.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.api.controller.dto.user.response.BookmarkMyPageResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "특정 북마크 폴더에 포함되는 모임들")
public class BookmarkEventResponseDto {

    @Schema(description = "모임 리스트")
    List<BookmarkMyPageResponseDto> bookmarkEventList;

}
