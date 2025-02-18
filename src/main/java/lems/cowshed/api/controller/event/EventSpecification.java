package lems.cowshed.api.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.config.swagger.ApiErrorCodeExample;
import lems.cowshed.config.swagger.ApiErrorCodeExamples;
import lems.cowshed.service.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface EventSpecification {

    @Operation(summary = "모임 목록 페이징 조회", description = "모임을 페이징 조회 합니다.")
    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<Slice<EventPreviewResponseDto>> getPagingEvents(Pageable pageable,
                                                                   @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "모임 등록", description = "모임을 등록 합니다.")
    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<Void> saveEvent(@RequestBody @Validated EventSaveRequestDto requestDto,
                                   @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "모임 상세 조회", description = "모임의 상세 정보를 반환 합니다.")
    @ApiErrorCodeExample(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<EventDetailResponseDto> getEvent(@PathVariable("event-id") Long eventId,
                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "모임 참여", description = "회원이 모임에 참여 합니다.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> saveUserEvent(@Parameter(name="event-id", description = "모임 id", example = "1") @PathVariable Long eventId
    , @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "모임 수정", description = "모임의 세부 사항을 수정 합니다.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> editEvent(@PathVariable("event-id") Long eventId,
                                   @RequestBody @Validated EventUpdateRequestDto eventUpdateDto,
                                   @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "모임 삭제", description = "모임을 삭제 합니다.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> deleteEvent(@PathVariable("event-id") Long eventId);

    @Operation(summary = "북마크 모임 페이징 조회", description = "북마크 모임 페이징 조회")
    CommonResponse<BookmarkResponseDto> getPagingBookmarkEvents(Pageable pageable,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails);

}