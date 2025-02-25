package lems.cowshed.api.controller.event;

import io.swagger.v3.oas.annotations.Parameter;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPagingResponse;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.service.CustomUserDetails;
import lems.cowshed.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController implements EventSpecification {

    private final EventService eventService;

    @GetMapping
    public CommonResponse<EventPagingResponse> getPagingEvents(@PageableDefault(page = 0, size = 10) Pageable pageable,
                                                               @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return CommonResponse.success(eventService.getPagingEvents(pageable, customUserDetails.getUserId()));
    }

    @GetMapping("/{event-id}")
    public CommonResponse<EventDetailResponseDto> getEvent(@PathVariable("event-id") Long eventId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        EventDetailResponseDto response = eventService.getEvent(eventId, userDetails.getUserId());
        return CommonResponse.success(response);
    }

    @GetMapping("/bookmarks")
    public CommonResponse<BookmarkResponseDto> getPagingBookmarkEvents(@PageableDefault(page = 0, size = 10) Pageable pageable,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails){
        BookmarkResponseDto bookmarkEvents = eventService.getPagingBookmarkEvents(pageable, userDetails.getUserId());
        return CommonResponse.success(bookmarkEvents);
    }

    @PostMapping
    public CommonResponse<Void> saveEvent(@RequestBody @Validated EventSaveRequestDto requestDto,
                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        eventService.saveEvent(requestDto, customUserDetails.getUsername());
        return CommonResponse.success();
    }

    @PostMapping("/{event-id}/join")
    public CommonResponse<Void> saveUserEvent(@PathVariable("event-id") Long eventId,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        eventService.joinEvent(eventId, customUserDetails.getUserId());
        return CommonResponse.success();
    }

    @PatchMapping("/{event-id}")
    public CommonResponse<Void> editEvent(@PathVariable("event-id") Long eventId,
                                          @RequestBody @Validated EventUpdateRequestDto requestDto,
                                          @AuthenticationPrincipal CustomUserDetails customUserDetails){
        eventService.editEvent(eventId, requestDto, customUserDetails.getUsername());
        return CommonResponse.success();
    }

    @DeleteMapping("/{event-id}")
    public CommonResponse<Void> deleteEvent(@PathVariable("event-id") Long eventId){
        eventService.deleteEvent(eventId);
        return CommonResponse.success();
    }

    @DeleteMapping("{event-id}/join")
    public CommonResponse<Void> deleteUserEvent(@PathVariable("event-id") Long eventId,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails){
        eventService.deleteUserEvent(eventId, customUserDetails.getUserId());
        return CommonResponse.success();
    }

}