package lems.cowshed.api.controller.event;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.event.response.EventListResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPageResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController implements EventSpecification {

    private final EventService eventService;

    @GetMapping
    public CommonResponse<EventPageResponseDto> findPagingEvents(@RequestParam int page, @RequestParam int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return null;
    }

    @PostMapping
    public CommonResponse<Void> saveEvent(@RequestBody @Validated EventSaveRequestDto requestDto) {
        return null;
    }

    @GetMapping("/{event-id}")
    public CommonResponse<EventDetailResponseDto> getEvent(@PathVariable("event-id") Long eventId) {
        return null;
    }

    @PatchMapping("/{event-id}")
    public CommonResponse<Void> editEvent(@PathVariable("event-id") Long eventId, @RequestBody @Validated EventUpdateRequestDto requestDto){
        return null;
    }

    //삭제
    @DeleteMapping("/{event-id}")
    public CommonResponse<Void> deleteEvent(@PathVariable("event-id") Long eventId){
        return null;
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