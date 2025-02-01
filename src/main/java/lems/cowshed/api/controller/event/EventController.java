package lems.cowshed.api.controller.event;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
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

    @PostMapping
    public CommonResponse<Void> saveEvent(@RequestBody @Validated EventSaveRequestDto requestDto) {
        eventService.saveEvent(requestDto);
        return CommonResponse.success();
    }

    @GetMapping("/{event-id}")
    public CommonResponse<EventDetailResponseDto> getEvent(@PathVariable("event-id") Long eventId) {
        EventDetailResponseDto response = eventService.getEvent(eventId);
        return CommonResponse.success(response);
    }

    @PostMapping("/{event-id}/join")
    public CommonResponse<Void> saveUserEvent(@PathVariable Long eventId) {
        return null;
    }

    @PatchMapping("/{event-id}")
    public CommonResponse<Void> editEvent(@PathVariable("event-id") Long eventId, @RequestBody @Validated EventUpdateRequestDto requestDto){
        eventService.editEvent(eventId, requestDto);
        return CommonResponse.success();
    }
    //삭제

    @DeleteMapping("/{event-id}")
    public CommonResponse<Void> deleteEvent(@PathVariable("event-id") Long eventId){
        eventService.deleteEvent(eventId);
        return CommonResponse.success();
    }

    //TODO
    /*카테고리별 조회
    @GetMapping("/category/{category}")
    public CommonResponse<EventListResponseDto> findByCategory(@PathVariable String category) {
        return null;
    }*/

    //TODO
    /*키워드로 조회
    @GetMapping("/keyword/{keyword}")
    public CommonResponse<EventListResponseDto> findByKeyword(@PathVariable String keyword) {
        return null;
    }*/
}