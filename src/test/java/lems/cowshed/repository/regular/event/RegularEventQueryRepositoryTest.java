package lems.cowshed.repository.regular.event;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.event.Category;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.repository.event.EventRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static lems.cowshed.domain.event.Category.GAME;
import static org.assertj.core.api.Assertions.assertThat;

class RegularEventQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private RegularEventQueryRepository regularEventQueryRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegularEventRepository regularEventRepository;

    @DisplayName("정기 모임을 검색할 때 모임 결과도 같이 조회 한다.")
    @Test
    void searchFetchEvent() {
        //given
        Event event = createEvent("테스터", "모임 이름", "내용", GAME);
        eventRepository.save(event);

        String searchContent = "정기 모임 이름";
        LocalDateTime dateTime = LocalDateTime.of(2025, 6, 11, 12, 0, 0);
        LocalDate searchDate = dateTime.toLocalDate();
        RegularEvent regularEvent = createRegularEvent(event, searchContent, "대구 동성로", dateTime, null);
        regularEventRepository.save(regularEvent);

        //when
        PageRequest pageRequest = PageRequest.of(0, 2);
        Slice<RegularEvent> result = regularEventQueryRepository.searchFetchEvent(pageRequest, searchContent, searchDate);

        //then
        List<RegularEvent> regularEvents = result.getContent();
        assertThat(regularEvents)
                .extracting("name", "location", "dateTime")
                .containsExactly(Tuple.tuple("정기 모임 이름", "대구 동성로", dateTime));

        Event findEvent = regularEvents.get(0).getEvent();
        assertThat(findEvent)
                .extracting("author", "name", "content")
                .containsExactly("테스터", "모임 이름", "내용");
    }

    private static Event createEvent(String author, String name, String content, Category category) {
        return Event.builder()
                .name(name)
                .author(author)
                .content(content)
                .category(category)
                .build();
    }

    private RegularEvent createRegularEvent(Event event, String name, String location, LocalDateTime dateTime, Long userId) {
        return RegularEvent.builder()
                .event(event)
                .name(name)
                .dateTime(dateTime)
                .capacity(50)
                .location(location)
                .userId(userId)
                .build();
    }
}