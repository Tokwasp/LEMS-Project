package lems.cowshed.api.controller.dto.bookmark.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
@Builder
@AllArgsConstructor
@Getter
@Schema(description = "북마크 목록 페이지에 표시될 모임 정보")
class BookmarkListResponseDto {
    @Schema(description = "모임 이름", example = "한강 러닝 모임")
    String eventName;

    @Schema(description = "등록자", example = "김철수")
    String author;

    @Schema(description = "모임 날짜", example = "2024-09-12")
    Date eventDate;

    @Schema(description = "수용 인원", example = "100")
    int capacity;

    @Schema(description = "참여 신청 인원", example = "50")
    int applicants;
}
