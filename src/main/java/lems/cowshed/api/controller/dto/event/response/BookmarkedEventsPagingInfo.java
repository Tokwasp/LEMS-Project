package lems.cowshed.api.controller.dto.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "북마크 모임 반환")
public class BookmarkedEventsPagingInfo {

    @Schema(description = "북마크 모임 리스트")
    List<EventSimpleInfo> bookmarks;

    boolean isLast;

    @Builder
    public BookmarkedEventsPagingInfo(List<EventSimpleInfo> bookmarks, boolean isLast) {
        this.bookmarks = bookmarks;
        this.isLast = isLast;
    }

    public static BookmarkedEventsPagingInfo of(List<EventSimpleInfo> bookmarks, boolean isLast){
        return BookmarkedEventsPagingInfo.builder()
                .bookmarks(bookmarks)
                .isLast(isLast)
                .build();
    }

}