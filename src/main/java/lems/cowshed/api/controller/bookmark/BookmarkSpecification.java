package lems.cowshed.api.controller.bookmark;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lems.cowshed.api.controller.dto.CommonResponse;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkEditRequestDto;
import lems.cowshed.api.controller.dto.user.response.BookmarkMyPageResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface BookmarkSpecification {
    @Operation(summary = "북마크 폴더 조회", description = "모든 북마크 폴더 이름을 조회합니다.")
    CommonResponse<List<String>> findAllBookmarkFolders();

    @Operation(summary = "북마크 폴더 등록", description = "새로운 북마크 폴더를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 폴더 등록 완료")})
    CommonResponse<Void> saveBookmarkFolder(@Schema(name = "folderName", description = "폴더 이름", example = "독서 모임 폴더") @RequestBody String folderName);

    @Operation(summary = "북마크 폴더 수정", description = "특정 북마크 폴더의 이름을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 폴더 수정 완료")})
    CommonResponse<Void> editBookmarkFolder(@RequestBody BookmarkEditRequestDto requestDto);

    @Operation(summary = "북마크 폴더 삭제", description = "특정 북마크 폴더를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 폴더 삭제 완료")})
    CommonResponse<Void> deleteBookmarkFolder(@Schema(name = "bookmarkId", description = "북마크 아이디", example = "1")@RequestBody Long bookmarkId);
}
