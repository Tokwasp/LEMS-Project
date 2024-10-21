package lems.cowshed.service;

import lems.cowshed.domain.event.EventRepository;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventServiceTest {

    @Autowired
    EventService eventService;

    @Mock
    EventRepository eventRepository;

//    @Test
//    void 모임목록_조회(){
//
//        //given
//        Long eventId = 1L;
//        String name = "kim";
//        String content = "같이 운동하실 분 구합니다. 같이 프레스 운동 하면서 서로 보조해주실 분 구합니다.";
//        Date eventDate = new Date();
//        int capacity = 20;
//        int applicants = 3;
//        Date createdDate = new Date();
//
//        EventListResponseDto dto = EventListResponseDto.builder()
//                .eventId(eventId)
//                .name(name)
//                .content(content)
//                .eventDate(eventDate)
//                .capacity(capacity)
//                .applicants(applicants)
//                .createdDate(createdDate)
//                .build();
//
//        Mockito.when(event);
//        //when
//        eventService.findAll();
//        //then
//    }

}