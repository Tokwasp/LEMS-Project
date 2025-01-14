package lems.cowshed.api.controller.bookmark;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.config.swagger.ApiErrorCodeExample;
import lems.cowshed.config.swagger.ApiErrorCodeExamples;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface BookmarkSpecification {
    @Operation(summary = "북마크 모임 페이징 조회", description = "북마크 모임 페이징 조회")
    @ApiErrorCodeExample(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<BookmarkResponseDto> getPageBookmarks(Pageable pageable);

    @Operation(summary = "북마크 추가", description = "회원이 모임을 북마크 합니다.")
    @ApiErrorCodeExamples({ErrorCode.SUCCESS, ErrorCode.NOT_FOUND_ERROR })
    CommonResponse<Void> saveBookmark(@PathVariable("event-id") Long eventId);

    @Operation(summary = "북마크 해제", description = "회원이 모임 북마크 해제 합니다.")
    @ApiErrorCodeExample(ErrorCode.SUCCESS)
    CommonResponse<Void> deleteBookmark(@PathVariable("id") long bookmarkId);
}
