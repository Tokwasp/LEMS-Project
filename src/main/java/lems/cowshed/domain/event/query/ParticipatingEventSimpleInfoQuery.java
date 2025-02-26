package lems.cowshed.domain.event.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;

@Data
@Schema(description = "마이 페이지 참여 모임 정보")
public class ParticipatingEventSimpleInfoQuery {

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

    @Schema(description = "최대 인원 수", example = "50")
    private int capacity;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "생성일", example = "2024-10-11")
    private LocalDateTime createdDateTime;

    @QueryProjection
    public ParticipatingEventSimpleInfoQuery(Long id, String author, String eventName,
                                             LocalDate eventDate, Long applicants, int capacity, LocalDateTime createdDateTime) {
        this.id = id;
        this.author = author;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.applicants = applicants;
        this.capacity = capacity;
        this.createdDateTime = createdDateTime;
    }

    public void statusBookmark(){
        this.status = BOOKMARK;
    }

    public void statusNotBookmark(){
        this.status = NOT_BOOKMARK;
    }

}