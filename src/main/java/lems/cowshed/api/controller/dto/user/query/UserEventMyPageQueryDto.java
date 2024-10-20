package lems.cowshed.api.controller.dto.user.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "마이 페이지 회원 북마크 정보")
public class UserEventMyPageQueryDto {

    @Schema(description = "이벤트 id", example = "1")
    private long id;

    @Schema(description = "주최자", example = "김철수")
    private String username;

    @Schema(description = "이벤트 이름", example = "자전거 소모임")
    private String eventName;

    @Schema(description = "이벤트 날짜", example = "2024-10-19")
    private LocalDateTime eventDate;
}
