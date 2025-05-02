package lems.cowshed.service.event;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.*;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.event.participation.EventParticipant;
import lems.cowshed.domain.event.participation.EventParticipantRepository;
import lems.cowshed.exception.NotFoundException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest extends IntegrationTestSupport {

    @Autowired
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventParticipationService eventParticipationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventParticipantRepository eventParticipantRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @BeforeEach
    void cleanUp() {
        eventParticipantRepository.deleteAllInBatch();
        bookmarkRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("모임을 등록 한다.")
    @Test
    void saveEvent() throws IOException {
        //given
        EventSaveRequestDto request = createRequestDto("자전거 모임", "서울", 10);

        //when
        eventService.saveEvent(request, "테스터");

        //then
        Event findEvent = eventRepository.findByName("자전거 모임");
        assertThat(findEvent).extracting("name", "capacity")
                .containsExactly("자전거 모임", 10);
    }

    @DisplayName("모임을 등록할때 업로드 파일이 없다면 업로드 파일은 null이다.")
    @Test
    void saveEvent_whenNotRegisterFile_UploadFileIsNull() throws IOException {
        //given
        EventSaveRequestDto request = createRequestDto("자전거 모임", "서울", 10);

        //when
        eventService.saveEvent(request, "테스터");

        //then
        Event findEvent = eventRepository.findByName("자전거 모임");
        assertThat(findEvent.getUploadFile()).isNull();
    }

    @DisplayName("모임을 조회 한다.")
    @Test
    void getEvent() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        //when
        EventInfo response = eventService.getEvent(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(response).extracting("name", "author", "bookmarkStatus", "isParticipated")
                .containsExactly("자전거 모임", "테스터", NOT_BOOKMARK, false);
    }

    @DisplayName("모임을 조회할때 회원이 참여한 모임은 참여 상태로 되어있다.")
    @Test
    void getEventWhenParticipatedEvent() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        Event event = createEvent("테스터", "자전거 모임");
        eventRepository.save(event);

        EventParticipant eventParticipant = EventParticipant.of(user, event);
        eventParticipantRepository.save(eventParticipant);

        //when
        EventInfo response = eventService.getEvent(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(response).extracting("bookmarkStatus", "isParticipated")
                .containsExactly(NOT_BOOKMARK, true);
    }

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
        EventInfo result = eventService.getEvent(event.getId(), user.getId(), user.getUsername());

        //then
        assertThat(result).isNotNull()
                .extracting("bookmarkStatus", "isParticipated")
                .containsExactly(BOOKMARK, false);
    }

    @DisplayName("두명의 회원이 하나의 모임에 참여 할때 모임의 참여자 수는 두명이다.")
    @Test
    void getEventsWithApplicants() {
        //given
        Event event = createEvent("테스터", "테스트 모임", 2);
        eventRepository.save(event);

        int userCount = 2;

        for(int i = 0; i < userCount; i++){
            User user = createUser("테스터", "testEmail");
            userRepository.save(user);
            eventParticipationService.saveEventParticipation(event.getId(), user.getId());
        }

        Pageable pageable = PageRequest.of(0, 1);

        //when
        EventsPagingInfo result = eventService.getEvents(pageable, 0L);

        //then
        assertThat(result.getContent()).hasSize(1)
                .extracting("applicants")
                .containsExactly(2L);
    }

    @DisplayName("페이징 정보를 받아 모임을 조회 합니다.")
    @Test
    void getEvents() {
        //given
        User user = createUser("테스터", "testEmail");
        userRepository.save(user);

        for (int i = 0; i < 10; i++) {
            Event event = createEvent("테스터" + i, "자전거 모임");
            eventRepository.save(event);
        }

        Pageable pageable = PageRequest.of(1, 3);

        //when
        EventsPagingInfo result = eventService.getEvents(pageable, user.getId());

        //then
        assertThat(result.getContent())
                .extracting("author")
                .containsExactlyInAnyOrder("테스터3", "테스터4", "테스터5");
    }

    @DisplayName("모임에 참여한 회원이 없을 경우 참여자 수는 0명이다.")
    @Test
    void getEventsWhenNothingApplicants() {
        //given
        Event event = createEvent("테스터", "테스트 모임", 2);
        eventRepository.save(event);

        Pageable pageable = PageRequest.of(0, 1);

        //when
        EventsPagingInfo result = eventService.getEvents(pageable, 0L);

        //then
        assertThat(result.getContent()).hasSize(1)
                .extracting("applicants")
                .containsExactly(0L);
    }

    @DisplayName("모임을 수정 한다.")
    @Test
    void editEvent() {
        //given
        Event event = createEvent("테스터", "자전거 모임", 10);
        eventRepository.save(event);

        EventUpdateRequestDto updateRequest = createUpdateRequestDto("산책 모임", 20);

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

        EventUpdateRequestDto updateRequest = createUpdateRequestDto("산책 모임", 20);

        //when //then
        assertThatThrownBy(() -> eventService.editEvent(event.getId(), updateRequest, "등록 안한 테스터"))
                .isInstanceOf(NotFoundException.class);
    }

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
        BookmarkedEventsPagingInfo bookmarkEvents = eventService.getEventsBookmarkedByUser(pageable, user.getId());

        //then
        assertThat(bookmarkEvents.getBookmarks())
                .hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrder("테스트0", "테스트1", "테스트2");
    }

    @DisplayName("북마크한 모임을 페이징 조회 할 때 북마크 여부를 확인 한다.")
    @Test
    void getEventsForBookmark() {
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
        List<EventSimpleInfo> result = eventService.getEvents(pageable, user.getId()).getContent();

        //then
        assertThat(result)
                .extracting("bookmarkStatus")
                .containsExactlyInAnyOrder(BOOKMARK, NOT_BOOKMARK);
    }

    @DisplayName("이름 혹은 내용에 검색어가 있는 모임을 조회 한다.")
    @Test
    void searchEventsByNameOrContent() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        String searchKeyword = "검색";
        Event eventWithKeywordInName = createEvent("테스터", "테스트 " + searchKeyword, "내용");
        Event eventWithKeywordInContent = createEvent("테스터", "모임", searchKeyword + " 테스트");
        Event dummyEvent = createEvent("dummyUser", "dummyName", "dummyContent");
        eventRepository.saveAll(List.of(eventWithKeywordInName,eventWithKeywordInContent,dummyEvent));

        //when
        List<EventSimpleInfo> result = eventService.searchEventsByNameOrContent(searchKeyword, user.getId()).getSearchResults();

        //then
        assertThat(result).hasSize(2)
                .extracting("name", "content", "bookmarkStatus")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("테스트 검색", "내용", NOT_BOOKMARK),
                        Tuple.tuple("모임", "검색 테스트", NOT_BOOKMARK)
                );
    }

    @DisplayName("모임을 검색할 때 회원이 북마크한 모임이라면 북마크 상태 이다.")
    @Test
    void searchEventsByNameOrContent_WhenUserHasBookmarkedEvent() {
        //given
        User user = createUser("테스터", "test@naver.com");
        User dummyUser = createUser("dummyUser", "test@naver.com");
        userRepository.saveAll(List.of(user, dummyUser));

        String searchKeyword = "모임";
        Event bookmarkedEvent = createEvent("테스터", searchKeyword, "북마크");
        Event notBookmarkedEvent = createEvent("테스터", searchKeyword, "북마크 NO");
        eventRepository.saveAll(List.of(bookmarkedEvent, notBookmarkedEvent));

        Bookmark bookmark = createBookmark(bookmarkedEvent, user);
        bookmarkRepository.save(bookmark);

        //when
        List<EventSimpleInfo> result = eventService.searchEventsByNameOrContent(searchKeyword, user.getId()).getSearchResults();

        //then
        assertThat(result).hasSize(2)
                .extracting("content", "bookmarkStatus")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("북마크", BOOKMARK),
                        Tuple.tuple("북마크 NO", NOT_BOOKMARK)
                );
    }

    @DisplayName("모임 검색 시 참여 인원 수가 정확히 반환 된다.")
    @Test
    void searchEventsByNameOrContent_WhenParticipantsAreCountedCorrectly() {
        //given
        User user1 = createUser("테스터", "test@naver.com");
        User user2 = createUser("테스터2", "test@naver.com");
        userRepository.saveAll(List.of(user1, user2));

        String searchKeyword = "모임";

        Event eventWithTwoParticipants = createEvent("테스터", searchKeyword);
        Event eventWithOneParticipant = createEvent("테스터2", searchKeyword);
        eventRepository.saveAll(List.of(eventWithTwoParticipants, eventWithOneParticipant));

        EventParticipant participation1 = EventParticipant.of(user1, eventWithTwoParticipants);
        EventParticipant participation2 = EventParticipant.of(user2, eventWithTwoParticipants);
        EventParticipant participation3 = EventParticipant.of(user1, eventWithOneParticipant);
        eventParticipantRepository.saveAll(List.of(participation1, participation2, participation3));

        //when
        List<EventSimpleInfo> result = eventService.searchEventsByNameOrContent(searchKeyword, user1.getId()).getSearchResults();

        //then
        assertThat(result).hasSize(2)
                .extracting("name", "applicants")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("모임", 2L),
                        Tuple.tuple("모임", 1L)
                );
    }

    private static Event createEvent(String author, String name) {
        return Event.builder()
                .name(name)
                .author(author)
                .build();
    }

    private static Event createEvent(String author, String name, String content){
        return Event.builder()
                .name(name)
                .author(author)
                .content(content)
                .build();
    }

    private static Event createEvent(String author, String name, int capacity) {
        return Event.builder()
                .name(name)
                .author(author)
                .capacity(capacity)
                .build();
    }

    private EventSaveRequestDto createRequestDto(String name, String location, int capacity) {
        return EventSaveRequestDto.builder()
                .name(name)
                .capacity(capacity)
                .build();
    }

    private EventUpdateRequestDto createUpdateRequestDto(String name, int capacity) {
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