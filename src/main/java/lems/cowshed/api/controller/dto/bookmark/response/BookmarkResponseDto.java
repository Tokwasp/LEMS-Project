package lems.cowshed.api.controller.dto.bookmark.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "북마크 폴더명 수정 요청 데이터")
public class BookmarkResponseDto {
    @Schema(description = "북마크 폴더 이름 리스트",
            example = "[\"등산 모임\", \"독서 모임\", \"축구 모임\"]")
    List<String> bookmarkFolderNames;
}
