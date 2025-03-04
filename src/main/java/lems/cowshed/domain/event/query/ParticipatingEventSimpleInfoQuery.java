package lems.cowshed.domain.event.query;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;

@Getter
@Schema(description = "마이 페이지 회원이 참여한 모임 정보")
public class ParticipatingEventSimpleInfoQuery {

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

    @Schema(description = "최대 인원 수", example = "50")
    private int capacity;

    @Schema(description = "생성일", example = "2024-10-11 20:30:10")
    private LocalDateTime createdDateTime;

    @Schema(description = "북마크 여부", example = "BOOKMARK")
    private BookmarkStatus bookmarkStatus;

    @QueryProjection
    public ParticipatingEventSimpleInfoQuery(Long id, String name, LocalDate eventDate, String author,
                                             String content, Long applicants, int capacity, LocalDateTime createdDateTime) {
        this.id = id;
        this.author = author;
        this.name = name;
        this.eventDate = eventDate;
        this.content = content;
        this.applicants = applicants;
        this.capacity = capacity;
        this.createdDateTime = createdDateTime;
    }

    public void statusBookmark(){
        this.bookmarkStatus = BOOKMARK;
    }

    public void statusNotBookmark(){
        this.bookmarkStatus = NOT_BOOKMARK;
    }

    public void setBookmarkStatus(BookmarkStatus bookmarkStatus) {
        this.bookmarkStatus = bookmarkStatus;
    }
}