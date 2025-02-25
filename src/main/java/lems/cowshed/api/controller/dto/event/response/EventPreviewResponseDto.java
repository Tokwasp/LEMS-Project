package lems.cowshed.api.controller.dto.event.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Schema(description = "메인 페이지의 모임 리스트 중 한 개의 모임 정보")
public class EventPreviewResponseDto {
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
    public EventPreviewResponseDto(Long id, String author, String eventName, LocalDate eventDate, BookmarkStatus status) {
        this.id = id;
        this.author = author;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.status = status;
    }

    @Builder
    public EventPreviewResponseDto(Long id, String author, String eventName, LocalDate eventDate, BookmarkStatus status, Long applicants) {
        this.id = id;
        this.author = author;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.status = status;
        this.applicants = applicants;
    }

    public static EventPreviewResponseDto of(Event event, long participantsCount, BookmarkStatus status){
        return EventPreviewResponseDto.builder()
                .id(event.getId())
                .eventName(event.getName())
                .author(event.getAuthor())
                .eventDate(event.getEventDate())
                .applicants(participantsCount)
                .status(status)
                .build();
    }

    public void changeApplicants(Long applicants){
        this.applicants = applicants;
    }
}