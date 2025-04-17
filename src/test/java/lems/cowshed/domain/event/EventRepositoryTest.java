package lems.cowshed.domain.event;

import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.userevent.UserEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class EventRepositoryTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserEventRepository userEventRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void cleanUp(){
        userEventRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("모임을 요청 받은 페이지의 사이즈 만큼 조회 한다.")
    @Test
    void findEventsBy() {
        //given
        for(int i = 0; i < 10; i++) {
            Event event = createEvent("테스터" + i);
            eventRepository.save(event);
        }

        Pageable pageable = PageRequest.of(1, 3);

        //when
        Slice<Event> slice = eventRepository.findEventsBy(pageable);

        //then
        assertThat(slice.getContent())
                .extracting("author")
                .containsExactlyInAnyOrder("테스터3", "테스터4", "테스터5");
    }

    @DisplayName("모임 페이징 정보를 조회 할때 다음 페이지 없는지 확인 한다.")
    @Test
    void findEventsByWhenNotExistNextPage() {
        //given
        for(int i = 0; i < 10; i++) {
            Event event = createEvent("테스터" + i);
            eventRepository.save(event);
        }

        Pageable pageable = PageRequest.of(3, 3);

        //when
        Slice<Event> slice = eventRepository.findEventsBy(pageable);

        //then
        assertThat(slice.hasNext()).isFalse();
    }

    private static Event createEvent(String author) {
        return Event.builder()
                .name("자전거 모임")
                .author(author)
                .build();
    }
}