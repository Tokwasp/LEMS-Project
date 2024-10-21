package lems.cowshed.api.controller.event;

import lems.cowshed.api.controller.dto.CommonResponse;
import lems.cowshed.api.controller.dto.event.response.EventListResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventResponseDto;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController implements EventSpecification {

    //모든 모임 조회
    @GetMapping
    public CommonResponse<List<EventListResponseDto>> findAll() {
        return null;
    }

    //카테고리별 조회
    @GetMapping("/{category}")
    public  CommonResponse<List<EventListResponseDto>> findByCategory(String category) {
        return null;
    }

    //키워드로 조회
    @GetMapping("/{keyword}")
    public CommonResponse<List<EventListResponseDto>> findByKeyword(String keyword) {
        List<EventListResponseDto> list = new ArrayList<>();
        return null;
    }

    //모임 상세 조회
    @GetMapping("/{eventId}")
    public CommonResponse<EventResponseDto> findById(@PathVariable Long eventId) {
        return null;
    }

    //등록
    @PostMapping
    public CommonResponse<Void> save(@RequestBody @Validated EventSaveRequestDto requestDto) {
        return CommonResponse.customMessage("등록 성공");
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