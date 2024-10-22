package lems.cowshed.api.controller.bookmark;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lems.cowshed.api.controller.dto.CommonResponse;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkDeleteRequestDto;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkEditRequestDto;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkSaveRequestDto;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface BookmarkSpecification {
    @Operation(summary = "북마크 폴더 조회", description = "모든 북마크 폴더 이름을 조회합니다.")
    CommonResponse<BookmarkResponseDto> findAllBookmarks();

    @Operation(summary = "북마크 폴더 등록", description = "새로운 북마크 폴더를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 폴더 등록 완료")})
    CommonResponse<Void> saveBookmark(@RequestBody BookmarkSaveRequestDto requestDto);

    @Operation(summary = "북마크 폴더 수정", description = "특정 북마크 폴더의 이름을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 폴더 수정 완료")})
    CommonResponse<Void> editBookmark(@RequestBody BookmarkEditRequestDto requestDto);

    @Operation(summary = "북마크 폴더 삭제", description = "특정 북마크 폴더를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 폴더 삭제 완료")})
    CommonResponse<Void> deleteBookmark(@RequestBody BookmarkDeleteRequestDto requestDto);
}
