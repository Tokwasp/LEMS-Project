package lems.cowshed.domain.event.query;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.api.controller.dto.event.EventIdProvider;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Schema(description = "마이 페이지 회원이 북마크한 모임 정보")
public class BookmarkedEventSimpleInfoQuery implements EventIdProvider {

    @Schema(description = "이벤트 id", example = "1")
    private Long id;

    @Schema(description = "모임 이름", example = "자전거 모임")
    private String name;

    @Schema(description = "이벤트 날짜", example = "2024-10-19")
    private LocalDate eventDate;

    @Schema(description = "주최자", example = "김철수")
    private String author;

    @Schema(description = "내용", example = "안녕하세요! 자전거 모임 입니다.")
    private String content;

    @Schema(description = "참여자 수 ", example = "15")
    private Long applicants;

    @Schema(description = "최대 인원수", example = "50")
    private int capacity;

    @Schema(description = "생성일", example = "2024-10-11 21:30:20")
    private LocalDateTime createdDateTime;

    @Schema(description = "북마크 여부", example = "BOOKMARK")
    private BookmarkStatus bookmarkStatus;

    @QueryProjection
    public BookmarkedEventSimpleInfoQuery(Long id, String name, LocalDate eventDate,
                                          String author, String content, int capacity,
                                          LocalDateTime createdDateTime, BookmarkStatus bookmarkStatus) {
        this.id = id;
        this.name = name;
        this.eventDate = eventDate;
        this.author = author;
        this.content = content;
        this.capacity = capacity;
        this.createdDateTime = createdDateTime;
        this.bookmarkStatus = bookmarkStatus;
    }

    @Builder
    public BookmarkedEventSimpleInfoQuery(Long id, String name, LocalDate eventDate,
                                          String author, String content, Long applicants,
                                          int capacity, LocalDateTime createdDateTime, BookmarkStatus bookmarkStatus) {
        this.id = id;
        this.name = name;
        this.eventDate = eventDate;
        this.author = author;
        this.content = content;
        this.applicants = applicants;
        this.capacity = capacity;
        this.createdDateTime = createdDateTime;
        this.bookmarkStatus = bookmarkStatus;
    }

    public static BookmarkedEventSimpleInfoQuery of(Event event, long participantsCount, BookmarkStatus bookmarkStatus){
        return BookmarkedEventSimpleInfoQuery.builder()
                .id(event.getId())
                .name(event.getName())
                .eventDate(event.getEventDate())
                .author(event.getAuthor())
                .content(event.getContent())
                .applicants(participantsCount)
                .capacity(event.getCapacity())
                .createdDateTime(event.getCreatedDateTime())
                .bookmarkStatus(bookmarkStatus)
                .build();
    }

    public static BookmarkedEventSimpleInfoQuery updateApplicants(BookmarkedEventSimpleInfoQuery dto, Long applicants){
        return BookmarkedEventSimpleInfoQuery.builder()
                .id(dto.getId())
                .name(dto.getName())
                .eventDate(dto.getEventDate())
                .author(dto.getAuthor())
                .content(dto.getContent())
                .applicants(applicants)
                .capacity(dto.getCapacity())
                .createdDateTime(dto.getCreatedDateTime())
                .bookmarkStatus(dto.bookmarkStatus)
                .build();
    }

    @Override
    public Long getEventId() {
        return this.id;
    }

    public void setApplicants(Long applicants) {
        this.applicants = applicants;
    }
}