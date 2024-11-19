package lems.cowshed.api.controller.dto.bookmark.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "북마크 폴더 삭제 요청 데이터")
public class BookmarkDeleteRequestDto {
    @Schema(description = "북마크 폴더 아이디", example = "1")
    Long bookmarkId;
}
