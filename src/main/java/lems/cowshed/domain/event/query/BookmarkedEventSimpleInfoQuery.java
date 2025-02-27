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
public class BookmarkedEventSimpleInfoQuery {

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

    @Schema(description = "최대 인원수", example = "50")
    private int capacity;

    @QueryProjection
    public BookmarkedEventSimpleInfoQuery(Long id, String author, String eventName,
                                          LocalDate eventDate, BookmarkStatus status, int capacity) {
        this.id = id;
        this.author = author;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.status = status;
        this.capacity = capacity;
    }

    @Builder
    public BookmarkedEventSimpleInfoQuery(Long id, String author, String eventName, LocalDate eventDate,
                                          BookmarkStatus status, Long applicants, int capacity) {
        this.id = id;
        this.author = author;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.status = status;
        this.applicants = applicants;
        this.capacity = capacity;
    }

    public static BookmarkedEventSimpleInfoQuery of(Event event, long participantsCount, BookmarkStatus status){
        return BookmarkedEventSimpleInfoQuery.builder()
                .id(event.getId())
                .eventName(event.getName())
                .author(event.getAuthor())
                .eventDate(event.getEventDate())
                .capacity(event.getCapacity())
                .applicants(participantsCount)
                .status(status)
                .build();
    }

}