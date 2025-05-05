package lems.cowshed.domain.event.query;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.api.controller.dto.event.response.EventSimpleInfo;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.RegularEventRepository;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.event.participation.EventParticipantRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static lems.cowshed.domain.bookmark.BookmarkStatus.BOOKMARK;
import static lems.cowshed.domain.user.Mbti.*;
import static org.assertj.core.api.Assertions.assertThat;

class EventQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RegularEventRepository regularEventRepository;

    @Autowired
    RegularEventParticipationRepository regularEventParticipationRepository;

    @Autowired
    EventQueryRepository eventQueryRepository;

    @Autowired
    EventParticipantRepository eventParticipantRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @BeforeEach
    public void cleanUp(){
        eventParticipantRepository.deleteAllInBatch();
        bookmarkRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
    }

    @DisplayName("모임과 모임 인원 정보를 함께 조회 한다.")
    @Test
    void findEventFetchParticipants() {
        //given
        User user = createUser("테스터", INTP);
        User user2 = createUser("테스터2", INTJ);
        userRepository.saveAll(List.of(user, user2));

        Event event = createEvent("산책 모임", "테스터");
        eventRepository.save(event);

        EventParticipation eventParticipation = EventParticipation.of(user, event);
        EventParticipation eventParticipation2 = EventParticipation.of(user2, event);
        eventParticipantRepository.saveAll(List.of(eventParticipation, eventParticipation2));

        //when
        Event findEvent = eventQueryRepository.findEventFetchParticipants(event.getId());

        //then
        List<EventParticipation> participants = findEvent.getParticipants();
        assertThat(participants).hasSize(2);
    }

    @DisplayName("정기 모임과 정기 모임 참여 정보를 함께 조회 한다.")
    @Test
    void findRegularEventsFetchParticipants() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);

        Event event = createEvent("산책 모임", "테스터");
        eventRepository.save(event);

        RegularEvent regularEvent = createRegularEvent(event, "테스트 모임", "테스트 장소");
        regularEventRepository.save(regularEvent);

        RegularEventParticipation regularEventParticipation = RegularEventParticipation.of(user, regularEvent);
        regularEventParticipationRepository.save(regularEventParticipation);

        //when
        List<RegularEvent> regularEvents = eventQueryRepository.findRegularEventsFetchParticipants(event.getId());

        //then
        assertThat(regularEvents).hasSize(1);
        assertThat(regularEvents.get(0).getName().equals("테스트 모임"));
    }

    @DisplayName("모임에 대한 정보와 참여 인원수를 조회 한다.")
    @Test
    void findEventWithApplicantUserIds() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);

        Event event = createEvent("산책 모임", "테스터");
        eventRepository.save(event);

        EventParticipation eventParticipation = EventParticipation.of(user, event);
        eventParticipantRepository.save(eventParticipation);

        //when
        List<ParticipatingEventSimpleInfoQuery> result = eventQueryRepository.findEventsParticipatedByUserWithApplicants(List.of(event.getId()));

        //then
        assertThat(result)
                .extracting("name", "applicants", "author")
                .containsExactly(Tuple.tuple("산책 모임", 1L, "테스터"));
    }

    @DisplayName("두명의 회원이 하나의 모임에 참여 하였을 때 참여자 수는 두명 이다.")
    @Test
    void findEventWithParticipatedWhenTwoUserIds() {
        //given
        User user = createUser("테스터", INTP);
        Event event = createEvent("산책 모임", "테스터");
        EventParticipation eventParticipation = EventParticipation.of(user, event);

        User user2 = createUser("테스터2", ESFJ);
        EventParticipation eventParticipation2 = EventParticipation.of(user2, event);
        userRepository.saveAll(List.of(user, user2));
        eventRepository.save(event);
        eventParticipantRepository.saveAll(List.of(eventParticipation, eventParticipation2));

        //when
        List<ParticipatingEventSimpleInfoQuery> response = eventQueryRepository.findEventsParticipatedByUserWithApplicants(List.of(event.getId()));

        //then
        assertThat(response).hasSize(1)
                .extracting("name", "applicants")
                .containsExactly(Tuple.tuple("산책 모임", 2L));
    }

    @DisplayName("회원이 북마크한 모임을 조회 할때 북마크 상태는 BOOKMARK 이다.")
    @Test
    void findBookmarkedEventsFromUser() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);
        Event event = createEvent("산책 모임", "테스터");
        eventRepository.save(event);

        Bookmark bookmark = createBookmark(event, user);
        bookmarkRepository.save(bookmark);

        //when
        List<BookmarkedEventSimpleInfoQuery> result = eventQueryRepository
                .findBookmarkedEventsFromUser(user.getId(), PageRequest.of(0, 5));

        //then
        assertThat(result).hasSize(1)
                .extracting("bookmarkStatus").containsExactly(BOOKMARK);
    }

    @DisplayName("모임 검색을 할때 모임 이름 혹은 내용이 포함된 모임을 찾는다.")
    @Test
    void searchEventsWithBookmarkStatus() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);
        Event event = createEvent("산책 모임", "테스터", "테스트 내용");
        eventRepository.save(event);

        Bookmark bookmark = createBookmark(event, user);
        bookmarkRepository.save(bookmark);

        //when
        List<EventSimpleInfo> result = eventQueryRepository.searchEventsWithBookmarkStatus("테스트", user.getId());

        //then
        assertThat(result).isNotEmpty()
                .extracting("name", "content", "bookmarkStatus")
                .containsExactly(Tuple.tuple("산책 모임", "테스트 내용", BOOKMARK));
    }

    private RegularEvent createRegularEvent(Event event, String name, String location){
        return RegularEvent.builder()
                .name(name)
                .event(event)
                .dateTime(LocalDateTime.of(2025,5,5,12,0,0))
                .location(location)
                .capacity(50)
                .build();
    }

    private Bookmark createBookmark(Event event, User user) {
        return Bookmark.builder()
                .event(event)
                .user(user)
                .status(BOOKMARK)
                .build();
    }

    private User createUser(String username, Mbti mbti) {
        return User.builder()
                .username(username)
                .mbti(mbti)
                .build();
    }

    private Event createEvent(String name, String author) {
        return Event.builder()
                .name(name)
                .author(author)
                .build();
    }

    private Event createEvent(String name, String author, String content){
        return Event.builder()
                .name(name)
                .content(content)
                .build();
    }
}