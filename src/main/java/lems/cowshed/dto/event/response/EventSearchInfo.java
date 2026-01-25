package lems.cowshed.dto.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import static lems.cowshed.domain.bookmark.BookmarkStatus.BOOKMARK;
import static lems.cowshed.domain.bookmark.BookmarkStatus.NOT_BOOKMARK;

@ToString
@Getter
public class EventSearchInfo {

    @Schema(description = "모임 id", example = "1")
    private Long id;

    @Schema(description = "모임 이름", example = "자전거 모임")
    private String name;

    @Schema(description = "내용", example = "여의도 한강공원 러닝크루 여의도 한강공원..")
    private String content;

    @Schema(description = "카테고리", example = "운동")
    private String category;

    @Schema(description = "참여 인원", example = "50")
    private int applicants;

    @Schema(description = "수용 최대 인원", example = "100")
    private int capacity;

    @Schema(description = "이미지", example = "www.xxx.com")
    private String accessUrl;

    @Schema(description = "북마크 여부 ", example = "BOOKMARK")
    private BookmarkStatus bookmarkStatus;

    @Builder
    private EventSearchInfo(Long id, String name, String content, String category,
                            int applicants, int capacity, String accessUrl, BookmarkStatus bookmarkStatus) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.category = category;
        this.applicants = applicants;
        this.capacity = capacity;
        this.accessUrl = accessUrl;
        this.bookmarkStatus = bookmarkStatus;
    }

    public static EventSearchInfo of(Event event, int participantCount, boolean isBookmarked) {
        return EventSearchInfo.builder()
                .id(event.getId())
                .name(event.getName())
                .content(event.getContent())
                .category(event.getCategory().getDescription())
                .capacity(event.getCapacity())
                .accessUrl(event.getUploadFile() != null ? event.getUploadFile().getRoute() : null)
                .applicants(participantCount)
                .bookmarkStatus(isBookmarked ? BOOKMARK : NOT_BOOKMARK)
                .build();
    }
}
