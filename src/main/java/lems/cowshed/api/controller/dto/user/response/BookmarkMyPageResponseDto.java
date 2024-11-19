package lems.cowshed.api.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "마이 페이지 하단에 표시되는 북마크 모임")
public class BookmarkMyPageResponseDto {

    @Schema(description = "북마크 id", example = "1")
    private Long bookmarkId;

    @Schema(description = "등록자", example = "김철수")
    private String author;

    @Schema(description = "모임 이름", example = "한강 러닝 모임")
    private String eventName;

    @Schema(description = "모임 날짜", example = "2024-09-12")
    private LocalDateTime eventDate;

    @Schema(description = "수용 인원", example = "100")
    private int capacity;

    @Schema(description = "참여 신청 인원", example = "50")
    private int applicants;
}
