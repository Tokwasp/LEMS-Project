package lems.cowshed.api.controller.bookmark;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkEditRequestDto;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkSaveRequestDto;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;

import lems.cowshed.config.swagger.ApiErrorCodeExample;
import lems.cowshed.config.swagger.ApiErrorCodeExamples;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface BookmarkSpecification {
    @Operation(summary = "북마크 폴더 조회", description = "모든 북마크 폴더 이름을 조회합니다.")
    CommonResponse<BookmarkResponseDto> getAllBookmarks();

    @Operation(summary = "북마크 폴더 등록", description = "새로운 북마크 폴더를 등록합니다.")
    @ApiErrorCodeExamples({ErrorCode.SUCCESS, ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> createBookmark(@RequestBody BookmarkSaveRequestDto requestDto);

    @Operation(summary = "북마크 폴더 수정", description = "특정 북마크 폴더의 이름을 수정합니다.")
    @ApiErrorCodeExamples({ErrorCode.SUCCESS, ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> editBookmarkName(@RequestBody BookmarkEditRequestDto requestDto, @PathVariable long bookmarkId);

    @Operation(summary = "북마크 폴더 삭제", description = "특정 북마크 폴더를 삭제합니다.")
    @ApiErrorCodeExample(ErrorCode.SUCCESS)
    CommonResponse<Void> deleteBookmark(@PathVariable long bookmarkId);
}
