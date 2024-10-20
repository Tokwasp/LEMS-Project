package lems.cowshed.api.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "마이 페이지 회원 정보")
public class BookmarkMyPageResponseDto {

    @Schema(description = "북마크 id", example = "1")
    private long id;

    @Schema(description = "주최자", example = "홍길동")
    private String username;

    @Schema(description = "이벤트 이름", example = "강아지 산책 소모임")
    private String eventName;

    @Schema(description = "이벤트 날짜", example = "2024-10-19")
    private LocalDateTime eventDate;
}
