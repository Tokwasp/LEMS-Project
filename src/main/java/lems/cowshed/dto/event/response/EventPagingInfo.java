package lems.cowshed.dto.event.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.dto.event.EventIdProvider;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "메인 모임 리스트중 하나의 모임")
public class EventPagingInfo implements EventIdProvider {

    @Schema(description = "모임 id", example = "1")
    private Long id;

    @Schema(description = "모임 이름", example = "자전거 모임")
    private String name;

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
    public EventPagingInfo(Long id, String name, String author, String content,
                           int capacity, LocalDateTime createdDateTime, BookmarkStatus bookmarkStatus){
        this.id = id;
        this.name = name;
        this.author = author;
        this.content = content;
        this.capacity = capacity;
        this.createdDateTime = createdDateTime;
        this.bookmarkStatus = bookmarkStatus;
    }

    @Builder
    public EventPagingInfo(Long id, String name, String author, String content, int capacity,
                           LocalDateTime createdDateTime, long applicants, BookmarkStatus bookmarkStatus) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.content = content;
        this.capacity = capacity;
        this.createdDateTime = createdDateTime;
        this.applicants = applicants;
        this.bookmarkStatus = bookmarkStatus;
    }

    public static EventPagingInfo of(Event event, long participantsCount, BookmarkStatus bookmarkStatus){
        return EventPagingInfo.builder()
                .id(event.getId())
                .name(event.getName())
                .author(event.getAuthor())
                .content(event.getContent())
                .capacity(event.getCapacity())
                .createdDateTime(event.getCreatedDateTime())
                .applicants(participantsCount)
                .bookmarkStatus(bookmarkStatus)
                .build();
    }

    @JsonIgnore
    @Override
    public Long getEventId() {
        return this.id;
    }
}