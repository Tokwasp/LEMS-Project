package lems.cowshed.dto.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.event.query.BookmarkedEventSimpleInfoQuery;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "북마크 모임 반환")
public class BookmarkedEventsPagingInfo {

    @Schema(description = "북마크 모임 리스트")
    List<BookmarkedEventSimpleInfoQuery> bookmarks;

    @Builder
    public BookmarkedEventsPagingInfo(List<BookmarkedEventSimpleInfoQuery> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public static BookmarkedEventsPagingInfo of(List<BookmarkedEventSimpleInfoQuery> bookmarks){
        return BookmarkedEventsPagingInfo.builder()
                .bookmarks(bookmarks)
                .build();
    }
}