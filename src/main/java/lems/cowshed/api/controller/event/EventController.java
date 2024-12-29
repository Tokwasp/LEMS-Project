package lems.cowshed.api.controller.event;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.event.response.EventListResponseDto;
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

    //모든 모임 조회
    @GetMapping
    public CommonResponse<Slice<EventPreviewResponseDto>> findAll(@RequestParam Long lastEventId, @PageableDefault(size = 30)Pageable pageable) {
        Slice<EventPreviewResponseDto> slice = eventService.findAll(lastEventId, pageable);
        return CommonResponse.success(slice);
    }

    //카테고리별 조회
    @GetMapping("/category/{category}")
    public CommonResponse<EventListResponseDto> findByCategory(@PathVariable String category) {
        return null;
    }

    //키워드로 조회
    @GetMapping("/keyword/{keyword}")
    public CommonResponse<EventListResponseDto> findByKeyword(@PathVariable String keyword) {
        return null;
    }

    //모임 상세 조회
    @GetMapping("/{eventId}")
    public CommonResponse<EventDetailResponseDto> findById(@PathVariable Long eventId) {
        return null;
    }

    //등록
    @PostMapping
    public CommonResponse<Void> save(@RequestBody @Validated EventSaveRequestDto requestDto) {
        return null;
    }

    //수정
    @PatchMapping("/{eventId}")
    public CommonResponse<Void> edit(@PathVariable Long eventId, @RequestBody @Validated EventUpdateRequestDto requestDto){
        return null;
    }

    //삭제
    @DeleteMapping("/{eventId}")
    public CommonResponse<Void> delete(@PathVariable Long eventId){
        return null;
    }
}