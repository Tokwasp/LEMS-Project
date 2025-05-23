package lems.cowshed.dto.bookmarkevent.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "특정 이벤트를 북마크에 등록 or 삭제 요청하는 데이터")
public class BookmarkEventRequestDto {
    @Schema(description = "이벤트 id", example = "1")
    Long eventId;

}
