package lems.cowshed.domain.user.query;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "마이 페이지 북마크 모임 정보")
public class UserBookmarkMyPageQueryDto {

    @Schema(description = "북마크 id", example = "1")
    private Long id;

    @Schema(description = "북마크 모임 이름", example = "강아지 산책 모임")
    private String bookmarkName;

    @Schema(description = "북마크 수정 날짜", example = "2024-10-19")
    private LocalDateTime modifiedDateTime;

    @QueryProjection
    public UserBookmarkMyPageQueryDto(Long id, String bookmarkName, LocalDateTime modifiedDateTime) {
        this.id = id;
        this.bookmarkName = bookmarkName;
        this.modifiedDateTime = modifiedDateTime;
    }
}