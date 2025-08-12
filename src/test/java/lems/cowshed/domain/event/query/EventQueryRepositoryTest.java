package lems.cowshed.domain.event.query;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.dto.event.response.query.EventParticipantQueryDto;
import lems.cowshed.repository.bookmark.BookmarkRepository;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.repository.event.participation.EventParticipantRepository;
import lems.cowshed.repository.event.query.BookmarkedEventSimpleInfoQuery;
import lems.cowshed.repository.event.query.EventQueryRepository;
import lems.cowshed.repository.event.query.ParticipatingEventSimpleInfoQuery;
import lems.cowshed.repository.regular.event.RegularEventRepository;
import lems.cowshed.repository.regular.event.participation.RegularEventParticipationRepository;
import lems.cowshed.repository.user.UserRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static lems.cowshed.domain.bookmark.BookmarkStatus.BOOKMARK;
import static lems.cowshed.domain.user.Mbti.ESFJ;
import static lems.cowshed.domain.user.Mbti.INTP;
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
    public void cleanUp() {
        eventParticipantRepository.deleteAllInBatch();
        bookmarkRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
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

        RegularEventParticipation regularEventParticipation = RegularEventParticipation.of(user.getId(), regularEvent);
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

        EventParticipation eventParticipation = EventParticipation.of(user, event.getId());
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
        User user2 = createUser("테스터2", ESFJ);
        userRepository.saveAll(List.of(user, user2));

        Event event = createEvent("산책 모임", "테스터");
        eventRepository.save(event);

        EventParticipation eventParticipation = EventParticipation.of(user, event.getId());
        EventParticipation eventParticipation2 = EventParticipation.of(user2, event.getId());
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

        Bookmark bookmark = createBookmark(user.getId());
        bookmark.connectEvent(event);
        bookmarkRepository.save(bookmark);

        //when
        List<BookmarkedEventSimpleInfoQuery> result = eventQueryRepository
                .findBookmarkedEventsFromUser(user.getId(), PageRequest.of(0, 5));

        //then
        assertThat(result).hasSize(1)
                .extracting("bookmarkStatus").containsExactly(BOOKMARK);
    }

    @DisplayName("모임에 참여한 회원들을 조회 한다.")
    @Test
    void getEventParticipants() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);
        Event event = createEvent("산책 모임", "테스터");
        eventRepository.save(event);
        EventParticipation eventParticipation = EventParticipation.of(user, event.getId());
        eventParticipantRepository.save(eventParticipation);

        //when
        List<EventParticipantQueryDto> result = eventQueryRepository.getEventParticipants(event.getId());

        //then
        assertThat(result.get(0))
                .extracting("name", "mbti")
                .containsExactly("테스터", INTP);
    }

    @DisplayName("모임에 참여한 회원들을 조회 할때 참여한 회원이 없다면 빈 리스트를 반환 한다.")
    @Test
    void getEventParticipants_WhenZeroParticipants_ThenReturnEmptyList() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);
        Event event = createEvent("산책 모임", "테스터");
        eventRepository.save(event);

        //when
        List<EventParticipantQueryDto> result = eventQueryRepository.getEventParticipants(event.getId());

        //then
        assertThat(result).isEmpty();
    }

    @DisplayName("모임에 참여한 회원들을 조회 할때 두명의 회원이 참여한 경우 모임의 참여 인원수는 2명 이다.")
    @Test
    void getEventParticipants_WhenTwoUsersParticipate_ThenCountIsTwo() {
        //given
        User user = createUser("테스터", INTP);
        User user2 = createUser("테스터2", ESFJ);
        userRepository.saveAll(List.of(user, user2));

        Event event = createEvent("산책 모임", "테스터");
        eventRepository.save(event);

        EventParticipation eventParticipation = EventParticipation.of(user, event.getId());
        EventParticipation eventParticipation2 = EventParticipation.of(user2, event.getId());

        eventParticipantRepository.saveAll(List.of(eventParticipation, eventParticipation2));

        //when
        List<EventParticipantQueryDto> result = eventQueryRepository.getEventParticipants(event.getId());

        //then
        assertThat(result).hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder("테스터", "테스터2");
    }

    private RegularEvent createRegularEvent(Event event, String name, String location) {
        return RegularEvent.builder()
                .name(name)
                .event(event)
                .dateTime(LocalDateTime.of(2025, 5, 5, 12, 0, 0))
                .location(location)
                .capacity(50)
                .build();
    }

    private Bookmark createBookmark(Long userId) {
        return Bookmark.builder()
                .userId(userId)
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
}