package lems.cowshed.domain.event.query;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.dto.event.EventIdProvider;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "마이 페이지 회원이 참여한 모임 정보")
public class ParticipatingEventSimpleInfoQuery implements EventIdProvider {

    @Schema(description = "이벤트 id", example = "1")
    private Long id;

    @Schema(description = "모임 이름", example = "자전거 모임")
    private String name;

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
    public ParticipatingEventSimpleInfoQuery(Long id, String name, String author,
                                             String content, Long applicants, int capacity,
                                             LocalDateTime createdDateTime) {
        this.id = id;
        this.author = author;
        this.name = name;
        this.content = content;
        this.applicants = applicants;
        this.capacity = capacity;
        this.createdDateTime = createdDateTime;
    }

    @Builder
    private ParticipatingEventSimpleInfoQuery(Long id, String name, String author, String content, Long applicants,
                                             int capacity, LocalDateTime createdDateTime, BookmarkStatus bookmarkStatus) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.content = content;
        this.applicants = applicants;
        this.capacity = capacity;
        this.createdDateTime = createdDateTime;
        this.bookmarkStatus = bookmarkStatus;
    }

    public static ParticipatingEventSimpleInfoQuery from(ParticipatingEventSimpleInfoQuery query, BookmarkStatus bookmarkStatus){
        return ParticipatingEventSimpleInfoQuery.builder()
                .id(query.getId())
                .name(query.getName())
                .author(query.getAuthor())
                .content(query.getContent())
                .applicants(query.getApplicants())
                .capacity(query.getCapacity())
                .createdDateTime(query.getCreatedDateTime())
                .bookmarkStatus(bookmarkStatus)
                .build();
    }

    @Override
    public Long getEventId() {
        return this.id;
    }

    public void setBookmarkStatus(BookmarkStatus bookmarkStatus) {
        this.bookmarkStatus = bookmarkStatus;
    }
}