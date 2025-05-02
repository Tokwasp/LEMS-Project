package lems.cowshed.service;

import lems.cowshed.api.controller.dto.recurring.event.RecurringEventSaveRequest;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.recurring.event.RecurringEvent;
import lems.cowshed.domain.recurring.event.RecurringEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class RecurringEventServiceTest {

    @Autowired
    private RecurringEventService recurringEventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RecurringEventRepository repository;

    @DisplayName("정기 모임을 등록 한다.")
    @Test
    void save() {
        //given
        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        RecurringEventSaveRequest request = RecurringEventSaveRequest.builder()
                .name("정기 모임")
                .date(LocalDate.of(2025, 5, 2))
                .location("테스트 장소")
                .capacity(50)
                .build();

        //when
        recurringEventService.save(request, event.getId());

        //then
        List<RecurringEvent> savedEvents = repository.findAll();
        assertThat(savedEvents).hasSize(1);
        assertThat(savedEvents.get(0).getName()).isEqualTo("정기 모임");
    }

    private static Event createEvent(String author, String name) {
        return Event.builder()
                .name(name)
                .author(author)
                .build();
    }
}