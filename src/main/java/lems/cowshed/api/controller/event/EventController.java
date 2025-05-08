package lems.cowshed.api.controller.event;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.event.response.*;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.EventParticipantsInfo;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController implements EventSpecification {

    private final EventService eventService;

    @GetMapping("/{event-id}")
    public CommonResponse<EventInfo> getEvent(@PathVariable("event-id") Long eventId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        EventInfo response = eventService.getEvent(eventId, userDetails.getUsername());
        return CommonResponse.success(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<Long> saveEvent(@ModelAttribute @Validated EventSaveRequestDto requestDto,
                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException {
        Long eventId = eventService.saveEvent(requestDto, customUserDetails.getUsername());
        return CommonResponse.success(eventId);
    }

    @PatchMapping("/{event-id}")
    public CommonResponse<Void> editEvent(@PathVariable("event-id") Long eventId,
                                          @RequestBody @Validated EventUpdateRequestDto requestDto,
                                          @AuthenticationPrincipal CustomUserDetails customUserDetails){
        eventService.editEvent(eventId, requestDto, customUserDetails.getUsername());
        return CommonResponse.success();
    }

    @GetMapping("/{event-id}/participants")
    public CommonResponse<EventParticipantsInfo> getEventParticipants(@PathVariable("event-id") Long eventId){
        EventParticipantsInfo participantsInfo = eventService.getEventParticipants(eventId);
        return CommonResponse.success(participantsInfo);
    }

    @GetMapping
    public CommonResponse<EventsPagingInfo> getEvents(@PageableDefault(page = 0, size = 10) Pageable pageable,
                                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        EventsPagingInfo events = eventService.getEvents(pageable, customUserDetails.getUserId());
        return CommonResponse.success(events);
    }

    @GetMapping("/{event-id}/regular")
    public CommonResponse<EventWithRegularInfo> getEventWithRegularInfo(@PathVariable("event-id") Long eventId,
                                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        EventWithRegularInfo response = eventService.getEventWithRegularInfo(eventId, userDetails.getUserId(), userDetails.getUsername());
        return CommonResponse.success(response);
    }

    @GetMapping("/bookmarks")
    public CommonResponse<BookmarkedEventsPagingInfo> getEventsBookmarkedByUser(@PageableDefault(page = 0, size = 10) Pageable pageable,
                                                                                @AuthenticationPrincipal CustomUserDetails userDetails){
        BookmarkedEventsPagingInfo bookmarkEvents = eventService.getEventsBookmarkedByUser(pageable, userDetails.getUserId());
        return CommonResponse.success(bookmarkEvents);
    }

    @GetMapping("/participating")
    public CommonResponse<ParticipatingEventsPagingInfo> getEventsParticipatedInUser(@PageableDefault(page = 0, size = 10) Pageable pageable,
                                                                                     @AuthenticationPrincipal CustomUserDetails userDetails){
        ParticipatingEventsPagingInfo ParticipatingEvents = eventService.getEventsParticipatedInUser(pageable, userDetails.getUserId());
        return CommonResponse.success(ParticipatingEvents);
    }

    @GetMapping("/search")
    public CommonResponse<EventsSearchInfo> searchEventsByNameOrContent(@RequestParam("keyword") String keyword,
                                                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        EventsSearchInfo searchEvent = eventService.searchEventsByNameOrContent(keyword, customUserDetails.getUserId());
        return CommonResponse.success(searchEvent);
    }

    @DeleteMapping("/{event-id}")
    public CommonResponse<Void> deleteEvent(@PathVariable("event-id") Long eventId,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        eventService.deleteEvent(eventId, customUserDetails.getUsername());
        return CommonResponse.success();
    }

}