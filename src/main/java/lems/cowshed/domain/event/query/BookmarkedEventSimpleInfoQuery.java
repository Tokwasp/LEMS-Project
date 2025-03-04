package lems.cowshed.domain.event.query;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Schema(description = "마이 페이지 회원이 북마크한 모임 정보")
public class BookmarkedEventSimpleInfoQuery {

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

    @Schema(description = "생성일", example = "2024-10-11")
    private LocalDate createdDate;

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
        this.createdDate = createdDateTime.toLocalDate();
        this.bookmarkStatus = bookmarkStatus;
    }

    @Builder
    public BookmarkedEventSimpleInfoQuery(Long id, String name, LocalDate eventDate,
                                          String author, String content, Long applicants,
                                          int capacity, LocalDate createdDate, BookmarkStatus bookmarkStatus) {
        this.id = id;
        this.name = name;
        this.eventDate = eventDate;
        this.author = author;
        this.content = content;
        this.applicants = applicants;
        this.capacity = capacity;
        this.createdDate = createdDate;
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
                .createdDate(event.getCreatedDateTime().toLocalDate())
                .bookmarkStatus(bookmarkStatus)
                .build();
    }

    public void setApplicants(Long applicants) {
        this.applicants = applicants;
    }
}