package lems.cowshed.service;

import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPagingResponse;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.userevent.UserEvent;
import lems.cowshed.domain.userevent.UserEventRepository;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.exception.NotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EventServiceTest {

    @Autowired
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserEventRepository userEventRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @BeforeEach
    void cleanUp() {
        userEventRepository.deleteAllInBatch();
        bookmarkRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Transactional
    @DisplayName("페이징 정보를 받아 모임을 조회 합니다.")
    @Test
    void getPagingEvents() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        for (int i = 0; i < 10; i++) {
            Event event = createEvent("테스터" + i, "자전거 모임");
            eventRepository.save(event);
        }

        Pageable pageable = PageRequest.of(1, 3);

        //when
        EventPagingResponse result = eventService.getPagingEvents(pageable, user.getId());

        //then
        assertThat(result.getContent())
                .extracting("author")
                .containsExactlyInAnyOrder("테스터3", "테스터4", "테스터5");
    }

    @Transactional
    @DisplayName("두명의 회원이 하나의 모임에 참여 할때 모임의 참여자 수는 두명이다.")
    @Test
    void getPagingEventsWithApplicants() {
        //given
        Event event = createEvent("테스터", "테스트 모임", 2);
        eventRepository.save(event);

        int userCount = 2;

        for(int i = 0; i < userCount; i++){
            User user = createUser("테스터", "testEmail");
            userRepository.save(user);
            eventService.joinEvent(event.getId(), user.getId());
        }

        Pageable pageable = PageRequest.of(0, 1);

        //when
        EventPagingResponse result = eventService.getPagingEvents(pageable, 0L);

        //then
        assertThat(result.getContent()).hasSize(1)
                .extracting("applicants")
                .containsExactly(2L);
    }

    @DisplayName("모임에 참여한 회원이 없을 경우 참여자 수는 0명이다.")
    @Test
    void getPagingEventsWhenNothingApplicants() {
        //given
        Event event = createEvent("테스터", "테스트 모임", 2);
        eventRepository.save(event);

        Pageable pageable = PageRequest.of(0, 1);

        //when
        EventPagingResponse result = eventService.getPagingEvents(pageable, 0L);

        //then
        assertThat(result.getContent()).hasSize(1)
                .extracting("applicants")
                .containsExactly(0L);
    }

    @Transactional
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

    @Transactional
    @DisplayName("모임을 조회 한다.")
    @Test
    void getEvent() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        //when
        EventDetailResponseDto response = eventService.getEvent(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(response).extracting("name", "author", "bookmarkStatus", "isParticipated")
                .containsExactly("자전거 모임", "테스터", NOT_BOOKMARK, false);
    }

    @DisplayName("모임을 조회할때 회원이 참여한 모임, 북마크한 모임은 표시가 되어있다.")
    @Test
    void getEventWhenParticipatedEvent() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        UserEvent userEvent = UserEvent.of(user, event);
        userEventRepository.save(userEvent);

        Bookmark bookmark = Bookmark.create(event, user, BOOKMARK);
        bookmarkRepository.save(bookmark);

        //when
        EventDetailResponseDto response = eventService.getEvent(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(response).extracting("bookmarkStatus", "isParticipated")
                .containsExactly(BOOKMARK, true);
    }

    @Transactional
    @DisplayName("북마크한 모임을 조회 할때 북마크의 상태는 BOOKMARK 이다.")
    @Test
    void getEventWhenBookmarked() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        Bookmark bookmark = createBookmark(event, user);
        bookmarkRepository.save(bookmark);

        //when
        EventDetailResponseDto result = eventService.getEvent(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(result).isNotNull()
                .extracting("bookmarkStatus")
                .isEqualTo(BOOKMARK);
    }

    @Transactional
    @DisplayName("모임을 수정 한다.")
    @Test
    void editEvent() {
        //given
        Event event = createEvent("테스터", "자전거 모임", 10);
        eventRepository.save(event);

        EventUpdateRequestDto updateRequest = createUpdateReqeustDto("산책 모임", 20);

        //when
        eventService.editEvent(event.getId(), updateRequest, "테스터");

        //then
        Event findEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(findEvent).extracting("author", "name", "capacity")
                .containsExactly("테스터", "산책 모임", 20);
    }

    @DisplayName("모임을 수정할 때 내가 등록한 모임이 아니라면 예외가 발생 한다.")
    @Test
    void editEventWhenNotRegisteredByMe() {
        //given
        Event event = createEvent("테스터", "자전거 모임", 10);
        eventRepository.save(event);

        EventUpdateRequestDto updateRequest = createUpdateReqeustDto("산책 모임", 20);

        //when //then
        assertThatThrownBy(() -> eventService.editEvent(event.getId(), updateRequest, "등록 안한 테스터"))
                .isInstanceOf(NotFoundException.class);
    }

    @Transactional
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

    @DisplayName("3명이 최대 인원인 모임에 5명이 참가 할때 두 회원은 참가 하지 못한다.")
    @Test
    void joinEventWhenNumberOfParticipantsExceeded() throws Exception {
        //given
        int taskCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);

        Event findEvent =  eventRepository.save(createEvent("테스터", "테스트 모임", 3));

        List<User> users = Stream
                .generate(() -> {
                    User user = createUser("테스터", "testEmail");
                    userRepository.save(user);
                    return user;
                })
                .limit(taskCount)
                .toList();

        //when
        AtomicInteger exceptionCount = new AtomicInteger(0);
        users.forEach((User user) -> {
                    try {
                        eventService.joinEvent(findEvent.getId(), user.getId());
                    } catch (BusinessException ex){
                        exceptionCount.incrementAndGet();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
        );
        countDownLatch.await();
        executorService.shutdown();

        //then
        long participants = userEventRepository.countParticipantByEventId(findEvent.getId());
        assertThat(participants).isEqualTo(3);
        assertThat(exceptionCount.get()).isEqualTo(2);
    }

    @DisplayName("5명의 회원이 동시에 최대 인원이 3명인 모임에 참가 할때 3명만 참여 할 수 있다.")
    @Test
    void joinEventWhenParticipateAtTheSameTimeWithConcurrency() throws Exception {
        //given
        int taskCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);

        Event findEvent =  eventRepository.save(createEvent("테스터", "테스트 모임", 3));

        List<User> users = Stream
                .generate(() -> {
                    User user = createUser("테스터", "testEmail");
                    userRepository.save(user);
                    return user;
                })
                .limit(taskCount)
                .toList();

        //when
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (User user : users) {
            executorService.submit(() -> {
                try {
                    eventService.joinEvent(findEvent.getId(), user.getId());
                } catch (BusinessException ex) {
                    exceptionCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();  // 카운트다운
                }
            });
        }
        countDownLatch.await();

        Long participateCount = executorService.submit(
                () -> userEventRepository.countParticipantByEventId(findEvent.getId())).get();

        executorService.shutdown();

        //then
        assertThat(participateCount).isEqualTo(3);
        assertThat(exceptionCount.get()).isEqualTo(2);
    }

    @Transactional
    @DisplayName("모임을 삭제 한다.")
    @Test
    void deleteEvent() {
        //given
        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        //when
        eventService.deleteEvent(event.getId(), event.getAuthor());

        //then
        assertThrows(NoSuchElementException.class,
                () -> eventRepository.findById(event.getId()).orElseThrow());
    }

    @Transactional
    @DisplayName("회원의 북마크한 모임을 페이징 조회 합니다.")
    @Test
    void getPagingBookmarkEvents() {
        //given
        String author = "테스터";
        User user = createUser(author, "test@naver.com");
        userRepository.save(user);

        int bookmarkCount = 3;
        for(int i = 0; i < bookmarkCount; i++){
            Event event = createEvent(author, "테스트" + i);
            eventRepository.save(event);

            Bookmark bookmark = createBookmark(event, user);
            bookmarkRepository.save(bookmark);
        }

        Pageable pageable = PageRequest.of(0, 3);

        //when
        BookmarkResponseDto bookmarkEvents = eventService.getPagingBookmarkEvents(pageable, user.getId());

        //then
        assertThat(bookmarkEvents.getBookmarks())
                .hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrder("테스트0", "테스트1", "테스트2");
    }

    @Transactional
    @DisplayName("북마크한 모임을 페이징 조회 할 때 북마크 여부를 확인 한다.")
    @Test
    void getPagingEventsForBookmark() {
        //given
        User user = createUser("북마크 테스터", "test@naver.com");
        userRepository.save(user);

        Event bookmarkEvent = createEvent("테스트", "북마크 모임");
        eventRepository.save(bookmarkEvent);

        Event unBookmarkEvent = createEvent("테스트", "북마크 안한 모임");
        eventRepository.save(unBookmarkEvent);

        Bookmark bookmark = createBookmark(bookmarkEvent, user);
        bookmarkRepository.save(bookmark);
        Pageable pageable = PageRequest.of(0, 2);

        //when
        List<EventPreviewResponseDto> result = eventService.getPagingEvents(pageable, user.getId()).getContent();

        //then
        assertThat(result)
                .extracting("bookmarkStatus")
                .containsExactlyInAnyOrder(BOOKMARK, NOT_BOOKMARK);
    }

    @Transactional
    @DisplayName("회원이 참석한 모임의 참석을 해제 한다.")
    @Test
    void deleteUserEvent() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        UserEvent userEvent = UserEvent.of(user, event);
        userEventRepository.save(userEvent);

        //when
        eventService.deleteUserEvent(event.getId(), user.getId());

        //then
        assertThatThrownBy(() -> userEventRepository.findById(userEvent.getId()).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("회원이 모임 참석을 해제 할때 등록 하지 않은 모임 이라면 예외가 발생 한다.")
    @Test
    void deleteUserEventWhenNotParticipated() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        Event event = createEvent("테스터", "테스트 모임");
        eventRepository.save(event);

        UserEvent userEvent = UserEvent.of(user, event);
        userEventRepository.save(userEvent);

        //when //then
        assertThatThrownBy(() -> eventService.deleteUserEvent(null, user.getId()))
                .isInstanceOf(NotFoundException.class);
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
        return EventUpdateRequestDto.builder()
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

    private Bookmark createBookmark(Event event, User user) {
        return Bookmark.builder()
                .event(event)
                .user(user)
                .status(BOOKMARK)
                .build();
    }

}