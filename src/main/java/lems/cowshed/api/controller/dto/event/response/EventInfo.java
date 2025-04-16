package lems.cowshed.api.controller.dto.event.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Schema(description = "모임 상세")
public class EventInfo {
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
    @Schema(description = "내용", example = "같이 운동하실 분 구합니다. 같이 프레스 운동 하면서 서로 보조해주실 분 구합니다.")
    String content;
    @Schema(description = "수용 인원", example = "100")
    int capacity;
    @Schema(description = "참여 신청 인원", example = "50")
    long applicants;
    @Schema(description = "북마크 여부", example = "BOOKMARK")
    BookmarkStatus bookmarkStatus;
    @Schema(description = "내가 등록한 모임 인지 여부", example = "true")
    boolean isEventRegistrant;
    @Schema(description = "내가 참여한 모임 인지 여부", example = "true")
    boolean isParticipated;

    @JsonIgnore
    String userList;

    @QueryProjection
    public EventInfo(Long eventId, String name, String author, Category category,
                     LocalDateTime createdDate, String content,
                     int capacity, long applicants, String userList) {
        this.eventId = eventId;
        this.name = name;
        this.author = author;
        this.category = category;
        this.createdDate = createdDate;
        this.content = content;
        this.capacity = capacity;
        this.applicants = applicants;
        this.userList = userList;
    }

    @Builder
    private EventInfo(Long eventId, String author, String name, Category category,
                      LocalDateTime createdDate,String content, int capacity, long applicants,
                      BookmarkStatus bookmarkStatus, boolean isEventRegistrant, boolean isParticipated) {
        this.eventId = eventId;
        this.name = name;
        this.author = author;
        this.category = category;
        this.createdDate = createdDate;
        this.content = content;
        this.capacity = capacity;
        this.applicants = applicants;
        this.bookmarkStatus = bookmarkStatus;
        this.isEventRegistrant = isEventRegistrant;
        this.isParticipated = isParticipated;
    }

    public boolean isEventRegistrant(String username) {
        return username.equals(author);
    }

    public void updateRegistrant(boolean isRegistrant) {
        if(isRegistrant) this.isEventRegistrant = true;
    }

    public void updateBookmarkStatus(BookmarkStatus bookmarkStatus) {
        this.bookmarkStatus = bookmarkStatus;
    }

    public void updateParticipated(boolean isParticipated) {
        this.isParticipated = isParticipated;
    }
}