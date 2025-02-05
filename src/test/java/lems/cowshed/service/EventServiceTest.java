package lems.cowshed.service;

import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.userevent.UserEvent;
import lems.cowshed.domain.userevent.UserEventRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class EventServiceTest {

    @Autowired
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserEventRepository userEventRepository;

    @DisplayName("페이징 정보를 받아 모임을 조회 합니다.")
    @Test
    void getPagingEvents() {
        //given
        for(int i = 0; i < 10; i++) {
            Event event = createEvent("테스터" + i, "자전거 모임");
            eventRepository.save(event);
        }

        Pageable pageable = PageRequest.of(1, 3);

        //when
        Slice<EventPreviewResponseDto> slice = eventService.getPagingEvents(pageable);

        //then
        assertThat(slice.getContent())
                .extracting("author")
                .containsExactlyInAnyOrder("테스터3", "테스터4", "테스터5");
    }

    @DisplayName("모임을 등록 한다.")
    @Test
    void saveEvent() {
        //given
        EventSaveRequestDto request = createRequestDto("자전거 모임", "서울", 10);

        //when
        eventService.saveEvent(request, "테스터");

        //then
        Event findEvent = eventRepository.findByName("자전거 모임");
        assertThat(findEvent).extracting("name", "location", "capacity")
                .containsExactly("자전거 모임", "서울", 10);
    }

    @DisplayName("모임을 조회 한다.")
    @Test
    void getEvent() {
        //given
        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        //when
        EventDetailResponseDto response = eventService.getEvent(event.getId());

        //then
        assertThat(response).extracting("name", "author")
                .containsExactly("자전거 모임", "테스터");
    }

    @DisplayName("모임을 수정 한다.")
    @Test
    void editEvent() {
        //given
        Event event = createEvent("테스터", "자전거 모임", 10);
        eventRepository.save(event);
        EventUpdateRequestDto updateRequest = createUpdateReqeustDto("산책 모임", 20);

        //when
        eventService.editEvent(event.getId(), updateRequest);

        //then
        Event findEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(findEvent).extracting("author", "name", "capacity")
                .containsExactly("테스터", "산책 모임", 20);
    }

    @DisplayName("유저가 모임에 참여 한다.")
    @Test
    void joinEvent() {
        //given
        Event event = createEvent("테스터", "자전거 모임", 10);
        eventRepository.save(event);

        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        //when
        long userEventId = eventService.joinEvent(event.getId(), user.getId());

        //then
        UserEvent userEvent = userEventRepository.findById(userEventId).orElseThrow();
        assertThat(userEvent.getUser()).extracting("username").isEqualTo("테스터");
        assertThat(userEvent.getEvent()).extracting("name").isEqualTo("자전거 모임");
    }

    @DisplayName("모임을 삭제 한다.")
    @Test
    void deleteEvent() {
        //given
        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        //when
        eventService.deleteEvent(event.getId());

        //then
        assertThrows(NoSuchElementException.class,
                () -> eventRepository.findById(event.getId()).orElseThrow());
    }

    private static Event createEvent(String author, String name) {
        return Event.builder()
                .name(name)
                .email("test@naver.com")
                .author(author)
                .build();
    }

    private static Event createEvent(String author, String name, int capacity) {
        return Event.builder()
                .name(name)
                .email("test@naver.com")
                .author(author)
                .capacity(capacity)
                .build();
    }
    private EventSaveRequestDto createRequestDto(String name, String location, int capacity) {
        return EventSaveRequestDto.builder()
                .name(name)
                .location(location)
                .capacity(capacity)
                .build();
    }

    private EventUpdateRequestDto createUpdateReqeustDto(String name, int capacity) {
        return  EventUpdateRequestDto.builder()
                .name(name)
                .capacity(capacity)
                .build();
    }

    private User createUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }
}