package lems.cowshed.api.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.config.swagger.ApiErrorCodeExample;
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
    public CommonResponse<Slice<EventPreviewResponseDto>> getPagingEvents(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return CommonResponse.success(eventService.getPagingEvents(pageable));
    }

    @GetMapping("/{event-id}")
    public CommonResponse<EventDetailResponseDto> getEvent(@PathVariable("event-id") Long eventId) {
        EventDetailResponseDto response = eventService.getEvent(eventId);
        return CommonResponse.success(response);
    }

    @GetMapping("/bookmark")
    public CommonResponse<BookmarkResponseDto> getAllBookmarkEvents(@AuthenticationPrincipal CustomUserDetails userDetails){
        BookmarkResponseDto response = eventService.getAllBookmarkEvents(userDetails.getUserId());
        return CommonResponse.success(response);
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
    public CommonResponse<Void> editEvent(@PathVariable("event-id") Long eventId, @RequestBody @Validated EventUpdateRequestDto requestDto){
        eventService.editEvent(eventId, requestDto);
        return CommonResponse.success();
    }


    @DeleteMapping("/{event-id}")
    public CommonResponse<Void> deleteEvent(@PathVariable("event-id") Long eventId){
        eventService.deleteEvent(eventId);
        return CommonResponse.success();
    }

}