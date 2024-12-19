package lems.cowshed.api.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lems.cowshed.api.controller.dto.CommonResponse;
import lems.cowshed.api.controller.dto.ErrorResponse;
import lems.cowshed.api.controller.dto.event.response.EventListResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface EventSpecification {
    @Operation(summary = "모든 모임 목록 조회", description = "모든 모임 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "❌ 요청이 잘못되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    CommonResponse<Slice<EventPreviewResponseDto>> findAll(@RequestParam Long lastEventId, Pageable pageable);

    @Operation(summary = "카테고리별 조회", description = "category에 해당하는 모임을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "❌ 요청이 잘못되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    CommonResponse<EventListResponseDto> findByCategory(@Parameter(name="category", description = "카테고리", example = "스포츠") @PathVariable String category);

    @Operation(summary = "검색어로 조회", description = "keyword가 포함된 모임을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "❌ 요청이 잘못되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    CommonResponse<EventListResponseDto> findByKeyword(@Parameter(name="keyword", description = "키워드", example = "축구") @PathVariable String keyword);

    @Operation(summary = "모임 상세 조회", description = "특정 모임의 상세 정보를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "❌ 요청이 잘못되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    CommonResponse<EventDetailResponseDto> findById(@Parameter(name="eventId", description = "모임 ID", example = "1") @PathVariable Long eventId);

    @Operation(summary = "모임 등록", description = "새로운 모임을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 모임 등록에 성공했습니다!"),
                    @ApiResponse(responseCode = "400", description = "❌ 모임 등록 값 검증에 실패했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    CommonResponse<Void> save(@RequestBody EventSaveRequestDto requestDto);

    @Operation(summary = "모임 수정", description = "모임의 세부 사항을 수정 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 모임 수정에 성공했습니다!"),
                    @ApiResponse(responseCode = "400", description = "❌ 모임 수정 요청이 잘못되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            })
    CommonResponse<Void> edit(@Parameter(name="eventId", description = "모임 ID", example = "1") @PathVariable Long eventId, @RequestBody @Validated EventUpdateRequestDto eventUpdateDto);

    @Operation(summary = "모임 삭제", description = "특정 모임을 삭제 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "⭕ SUCCESS 모임 삭제에 성공했습니다!"),
                    @ApiResponse(responseCode = "400", description = "❌ 모임 삭제 요청이 잘못되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    CommonResponse<Void> delete(@Parameter(name="eventId", description = "모임 ID", example = "1") @PathVariable Long eventId);

}
