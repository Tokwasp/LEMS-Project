package lems.cowshed.api.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.api.controller.dto.event.response.*;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.EventParticipantsInfo;
import lems.cowshed.config.swagger.ApiErrorCodeExample;
import lems.cowshed.config.swagger.ApiErrorCodeExamples;
import lems.cowshed.domain.user.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name="event", description="모임 API")
public interface EventSpecification {

    @Operation(summary = "모임 목록 페이징 조회", description = "모임을 페이징 조회 합니다.")
    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<EventsPagingInfo> getEvents(Pageable pageable,
                                               @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "모임 등록", description = "모임을 등록 합니다.")
    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<Long> saveEvent(@ModelAttribute @Validated EventSaveRequestDto requestDto,
                                   @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException;

    @Operation(summary = "모임 조회", description = "모임을 조회 합니다.")
    CommonResponse<EventInfo> getEvent(@PathVariable("event-id") Long eventId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "모임에 참여한 회원 조회", description = "특정 모임에 참여한 회원들을 조회 합니다.")
    CommonResponse<EventParticipantsInfo> getEventParticipants(@PathVariable("event-id") Long eventId);

    @Operation(summary = "모임 / 정기모임 상세 조회", description = "모임과 정기 모임 상세 정보를 반환 합니다.")
    @ApiErrorCodeExample(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<EventWithRegularInfo> getEventWithRegularInfo(@PathVariable("event-id") Long eventId,
                                                                 @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "모임 수정", description = "모임의 세부 사항을 수정 합니다.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> editEvent(@PathVariable("event-id") Long eventId,
                                   @RequestBody @Validated EventUpdateRequestDto eventUpdateDto,
                                   @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "모임 삭제", description = "모임을 삭제 합니다.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> deleteEvent(@PathVariable("event-id") Long eventId,
                                     @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "북마크 모임 페이징 조회", description = "북마크 모임 페이징 조회 합니다.")
    CommonResponse<BookmarkedEventsPagingInfo> getEventsBookmarkedByUser(@PageableDefault(page = 0, size = 10) Pageable pageable,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "참여 모임 페이징 조회", description = "참여 모임 페이징 조회 합니다.")
    CommonResponse<ParticipatingEventsPagingInfo> getEventsParticipatedInUser(@PageableDefault(page = 0, size = 10) Pageable pageable,
                                                                              @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "모임 검색", description = "해당 제목 혹은 내용이 포함된 모임을 검색 합니다.")
    CommonResponse<EventsSearchInfo> searchEventsByNameOrContent(@RequestParam("keyword") String keyword,
                                                                 @AuthenticationPrincipal CustomUserDetails customUserDetails);
}