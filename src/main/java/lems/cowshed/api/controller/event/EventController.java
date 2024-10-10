package lems.cowshed.api.controller.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.api.dto.event.EventDto;
import lems.cowshed.api.dto.event.EventListDto;
import lems.cowshed.api.dto.event.EventSaveDto;
import lems.cowshed.api.dto.event.EventUpdateDto;
import lems.cowshed.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/events")
public class EventController implements EventSpecification {

    @Autowired
    private EventService eventService;
    
    //모든 모임 목록 조회
    @GetMapping("/")
    public List<EventListDto> findAll() { 
        return eventService.findAll();
    }
    
    //카테고리별 조회
    @GetMapping("/{category}")
    public List<EventListDto> findByCategory(@PathVariable String category) { 
        return eventService.findByCategory(category);
    }

    //검색어로 조회
    @GetMapping("/{keyword}")
    public List<EventListDto> findByKeyword(@PathVariable String keyword) { 
        return eventService.findByKeyword(keyword);
    }

    //모임 상세 조회
    @GetMapping("/{id}")
    public EventDto findById(@PathVariable Long id) { 
        return eventService.findById(id);
    }

    //등록
    @PostMapping
    public void save(@RequestBody @Validated EventSaveDto eventSaveDto) {
        eventService.save(eventSaveDto);
    }

    //수정
    @PatchMapping("/{id}")
    public void edit(@PathVariable Long id, @RequestBody @Validated EventUpdateDto eventUpdateDto){
        eventService.edit(id);
    }

    //삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        eventService.delete(id);
    }
}