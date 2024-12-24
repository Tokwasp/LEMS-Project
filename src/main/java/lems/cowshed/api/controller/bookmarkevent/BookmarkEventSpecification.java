package lems.cowshed.api.controller.bookmarkevent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.bookmarkevent.request.BookmarkEventRequestDto;
import lems.cowshed.api.controller.dto.bookmarkevent.response.BookmarkEventResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface BookmarkEventSpecification {
    @Operation(summary = "북마크 상세 조회", description = "특정 폴더 내에 속한 북마크들을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 북마크 조회 완료")})
    CommonResponse<BookmarkEventResponseDto> findAllBookmarkEvents(@Parameter(name="bookmarkId", description = "북마크 id", example = "1") @PathVariable Long bookmarkId);

    @Operation(summary = "북마크 모임 등록", description = "특정 폴더에 새로운 모임을 포함시킵니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 북마크 등록 완료")})
    CommonResponse<Void> saveBookmarkEvent(@Parameter(name="bookmarkId", description = "북마크 id", example = "1") @PathVariable Long bookmarkId, @RequestBody BookmarkEventRequestDto requestDto);


    @Operation(summary = "북마크 모임 삭제", description = "특정 모임을 특정 북마크에 포함시키지 않습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 북마크 삭제 완료")})
    CommonResponse<Void> deleteBookmarkEvent(@Parameter(name="bookmarkId", description = "북마크 id", example = "1") @PathVariable Long bookmarkId, @RequestBody BookmarkEventRequestDto requestDto);

}
