package lems.cowshed.api.controller.event;

import lems.cowshed.api.controller.dto.CommonResponse;
import lems.cowshed.api.controller.dto.event.EventDto;
import lems.cowshed.api.controller.dto.event.EventSaveDto;
import lems.cowshed.api.controller.dto.event.EventUpdateDto;
//import lems.cowshed.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController implements EventSpecification {

 /*   @Autowired
    private EventService eventService;*/
    
   /* //모든 모임 목록 조회
    @GetMapping("/")
    public List<EventListDto> findAll() {
        return null;
        //return eventService.findAll();
    }*/
    
   /* //카테고리별 조회
    @GetMapping("/{category}")
    public List<EventListDto> findByCategory(@PathVariable String category) {
        return null;
        *//*return eventService.findByCategory(category);*//*
    }*/

    /*//검색어로 조회
    @GetMapping("/{keyword}")
    public List<EventListDto> findByKeyword(@PathVariable String keyword) {
        return null;
        *//*return eventService.findByKeyword(keyword);*//*
    }*/

    //모임 상세 조회
    @GetMapping("/{id}")
    public CommonResponse<EventDto> findById(@PathVariable Long id) {
        EventDto eventDto = new EventDto();
        return CommonResponse.customMessage(eventDto, "조회 성공");
    }

    //등록
    @PostMapping
    public CommonResponse<Void> save(@RequestBody @Validated EventSaveDto eventSaveDto) {
        return CommonResponse.customMessage("등록 성공");
    }

    //수정
    @PatchMapping("/{id}")
    public CommonResponse<Void> edit(@PathVariable Long id, @RequestBody @Validated EventUpdateDto eventUpdateDto){
        return CommonResponse.customMessage("수정 성공");
    }

    //삭제
    @DeleteMapping("/{id}")
    public CommonResponse<Void> delete(@PathVariable Long id){
        return CommonResponse.customMessage("삭제 성공");
    }
}