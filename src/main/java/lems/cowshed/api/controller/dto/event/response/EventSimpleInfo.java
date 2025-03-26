package lems.cowshed.api.controller.dto.event.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.api.controller.dto.event.EventIdProvider;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static lems.cowshed.domain.bookmark.BookmarkStatus.BOOKMARK;
import static lems.cowshed.domain.bookmark.BookmarkStatus.NOT_BOOKMARK;

@Getter
@Schema(description = "메인 모임 리스트중 하나의 모임")
public class EventSimpleInfo implements EventIdProvider {

    @Schema(description = "모임 id", example = "1")
    private Long id;

    @Schema(description = "모임 이름", example = "자전거 모임")
    private String name;

    @Schema(description = "모임 날짜", example = "2024-09-12")
    private LocalDate eventDate;

    @Schema(description = "모임 등록자", example = "김철수")
    private String author;

    @Schema(description = "내용", example = "같이 운동하실 분 구합니다. 같이 프레스 운동 하면서 서로 보조해주실 분 구합니다.")
    private String content;

    @Schema(description = "참여 인원", example = "50")
    private long applicants;

    @Schema(description = "수용 최대 인원", example = "100")
    private int capacity;

    @Schema(description = "등록일", example = "2015-10-03 21:30:20")
    private LocalDateTime createdDateTime;

    @Schema(description = "북마크 여부 ", example = "Y")
    private BookmarkStatus bookmarkStatus;

    @QueryProjection
    public EventSimpleInfo(Long id, String name, String author,
                           String content, LocalDate eventDate,
                           int capacity, LocalDateTime createdDateTime, BookmarkStatus bookmarkStatus){
        this.id = id;
        this.name = name;
        this.author = author;
        this.content = content;
        this.eventDate = eventDate;
        this.capacity = capacity;
        this.createdDateTime = createdDateTime;
        this.bookmarkStatus = bookmarkStatus;
    }

    @Builder
    public EventSimpleInfo(Long id, String name, String author,
                           String content, LocalDate eventDate, int capacity,
                           LocalDateTime createdDateTime, long applicants, BookmarkStatus bookmarkStatus) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.content = content;
        this.eventDate = eventDate;
        this.capacity = capacity;
        this.createdDateTime = createdDateTime;
        this.applicants = applicants;
        this.bookmarkStatus = bookmarkStatus;
    }

    public static EventSimpleInfo of(Event event, long participantsCount, BookmarkStatus bookmarkStatus){
        return EventSimpleInfo.builder()
                .id(event.getId())
                .name(event.getName())
                .author(event.getAuthor())
                .content(event.getContent())
                .eventDate(event.getEventDate())
                .capacity(event.getCapacity())
                .createdDateTime(event.getCreatedDateTime())
                .applicants(participantsCount)
                .bookmarkStatus(bookmarkStatus)
                .build();
    }

    @Override
    public Long getEventId() {
        return this.id;
    }

    public void changeApplicants(Long applicants){
        this.applicants = applicants;
    }

    public void updateBookmarkStatus(BookmarkStatus bookmarkStatus){
        this.bookmarkStatus = bookmarkStatus;
    }

}