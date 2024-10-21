package lems.cowshed.api.controller.dto.bookmark.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "북마크 폴더명 수정 요청 데이터")
public class BookmarkEditRequestDto{
    @Schema(description = "수정할 북마크 폴더 id", example = "1")
    Long bookmarkId;

    @Schema(description = "새로운 북마크 폴더 이름", example = "새로운 폴더")
    String NewBookmarkFolderName;
}