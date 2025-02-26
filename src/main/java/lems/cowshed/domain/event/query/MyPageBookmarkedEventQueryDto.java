package lems.cowshed.domain.event.query;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "마이 페이지 북마크 모임 정보")
public class MyPageBookmarkedEventQueryDto {

    @Schema(description = "이벤트 id", example = "1")
    private Long id;

    @Schema(description = "주최자", example = "김철수")
    private String author;

    @Schema(description = "이벤트 이름", example = "자전거 소모임")
    private String eventName;

    @Schema(description = "이벤트 날짜", example = "2024-10-19")
    private LocalDate eventDate;

    @Schema(description = "북마크 여부", example = "BOOKMARK")
    private BookmarkStatus status;

    @Schema(description = "참여자 수 ", example = "15")
    private Long applicants;

    @QueryProjection
    public MyPageBookmarkedEventQueryDto(Long id, String author, String eventName, LocalDate eventDate, BookmarkStatus status) {
        this.id = id;
        this.author = author;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.status = status;
    }

    @Builder
    public MyPageBookmarkedEventQueryDto(Long id, String author, String eventName, LocalDate eventDate, BookmarkStatus status, Long applicants) {
        this.id = id;
        this.author = author;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.status = status;
        this.applicants = applicants;
    }

    public static MyPageBookmarkedEventQueryDto of(Event event, long participantsCount, BookmarkStatus status){
        return MyPageBookmarkedEventQueryDto.builder()
                .id(event.getId())
                .eventName(event.getName())
                .author(event.getAuthor())
                .eventDate(event.getEventDate())
                .applicants(participantsCount)
                .status(status)
                .build();
    }

}