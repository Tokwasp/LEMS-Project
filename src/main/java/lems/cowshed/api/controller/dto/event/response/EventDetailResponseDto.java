package lems.cowshed.api.controller.dto.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Category;
import lems.cowshed.domain.event.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Schema(description = "모임 상세")
public class EventDetailResponseDto {
    @Schema(description = "모임 id", example = "1")
    Long eventId;
    @Schema(description = "모임 이름", example = "농구 모임")
    String name;
    @Schema(description = "만든이", example = "김길동")
    String author;
    @Schema(description = "카테고리", example = "스포츠")
    Category category;
    @Schema(description = "등록일", example = "yyyy-mm-dd hh:mm:ss")
    LocalDateTime createdDate;
    @Schema(description = "장소 이름", example = "스카이 휘트니스")
    String location;
    @Schema(description = "내용", example = "같이 운동하실 분 구합니다. 같이 프레스 운동 하면서 서로 보조해주실 분 구합니다.")
    String content;
    @Schema(description = "모임 날짜", example = "2024-09-12")
    LocalDate eventDate;
    @Schema(description = "수용 인원", example = "100")
    int capacity;
    @Schema(description = "참여 신청 인원", example = "50")
    long applicants;
    @Schema(description = "북마크 여부", example = "BOOKMARK")
    BookmarkStatus bookmarkStatus;
    @Schema(description = "내가 등록한 모임 인지 여부", example = "YES")
    boolean registeredByMe;
    @Schema(description = "내가 참여한 모임 인지 여부", example = "YES")
    boolean isParticipated;

    @Builder
    private EventDetailResponseDto(Long eventId, String author, String name, Category category,
                                  LocalDateTime createdDate, String location,
                                  String content, LocalDate eventDate, int capacity, long applicants,
                                   BookmarkStatus bookmarkStatus, boolean registeredByMe, boolean isParticipated) {
        this.eventId = eventId;
        this.name = name;
        this.author = author;
        this.category = category;
        this.createdDate = createdDate;
        this.location = location;
        this.content = content;
        this.eventDate = eventDate;
        this.capacity = capacity;
        this.applicants = applicants;
        this.bookmarkStatus = bookmarkStatus;
        this.registeredByMe = registeredByMe;
        this.isParticipated = isParticipated;
    }

    public static EventDetailResponseDto from(Event event, long participantsCount, BookmarkStatus bookmarkStatus,
                                              boolean registeredByMe, boolean isParticipated){
        return EventDetailResponseDto.builder()
                .eventId(event.getId())
                .name(event.getName())
                .author(event.getAuthor())
                .category(event.getCategory())
                .createdDate(event.getCreatedDateTime())
                .location(event.getLocation())
                .content(event.getContent())
                .eventDate(event.getEventDate())
                .capacity(event.getCapacity())
                .applicants(participantsCount)
                .bookmarkStatus(bookmarkStatus)
                .registeredByMe(registeredByMe)
                .isParticipated(isParticipated)
                .build();
    }
}