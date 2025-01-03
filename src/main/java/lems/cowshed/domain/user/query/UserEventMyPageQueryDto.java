package lems.cowshed.domain.user.query;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "마이 페이지 참여 모임 정보")
public class UserEventMyPageQueryDto {

    @Schema(description = "이벤트 id", example = "1")
    private Long id;

    @Schema(description = "주최자", example = "김철수")
    private String author;

    @Schema(description = "이벤트 이름", example = "자전거 소모임")
    private String eventName;

    @Schema(description = "이벤트 날짜", example = "2024-10-19")
    private LocalDateTime eventDate;

    @QueryProjection
    public UserEventMyPageQueryDto(Long id, String author, String eventName, LocalDateTime eventDate) {
        this.id = id;
        this.author = author;
        this.eventName = eventName;
        this.eventDate = eventDate;
    }
}
