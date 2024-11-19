package lems.cowshed.api.controller.dto.bookmark.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "북마크 폴더 등록 요청 데이터")
public class BookmarkSaveRequestDto {
    @Schema(description = "북마크 폴더 이름", example = "기본 폴더")
    String bookmarkFolderName;
}
