package lems.cowshed.domain.event.query;

import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventJpaRepository;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.userevent.UserEvent;
import lems.cowshed.domain.userevent.UserEventRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lems.cowshed.domain.bookmark.BookmarkStatus.BOOKMARK;
import static lems.cowshed.domain.user.Mbti.ESFJ;
import static lems.cowshed.domain.user.Mbti.INTP;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class EventQueryRepositoryTest {

    @Autowired
    EventJpaRepository eventJpaRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventQueryRepository eventQueryRepository;

    @Autowired
    UserEventRepository userEventRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @BeforeEach
    public void cleanUp(){
        userEventRepository.deleteAllInBatch();
        bookmarkRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        eventJpaRepository.deleteAllInBatch();
    }

    @DisplayName("회원이 참여한 모임을 조회 할때 참여자 수를 조회 한다.")
    @Test
    void findParticipatedEvents() {
        //given
        User user = createUser("테스터", INTP);
        Event event = createEvent("산책 모임", "테스터");
        UserEvent userEvent = UserEvent.of(user, event);

        User user2 = createUser("테스터2", ESFJ);
        UserEvent userEvent2 = UserEvent.of(user2, event);
        userRepository.saveAll(List.of(user, user2));
        eventJpaRepository.save(event);
        userEventRepository.saveAll(List.of(userEvent,userEvent2));

        //when
        List<ParticipatingEventSimpleInfoQuery> response = eventQueryRepository.findParticipatedEvents(List.of(event.getId()));

        //then
        assertThat(response).hasSize(1)
                .extracting("eventName", "applicants")
                .containsExactly(Tuple.tuple("산책 모임", 2L));
    }

    @DisplayName("회원이 북마크한 모임을 조회 할때 북마크 상태는 BOOKMARK 이다.")
    @Test
    void findBookmarkedEvents() {
        //given
        User user = createUser("테스터", INTP);
        userRepository.save(user);
        Event event = createEvent("산책 모임", "테스터");
        eventJpaRepository.save(event);

        Bookmark bookmark = createBookmark(event, user);
        bookmarkRepository.save(bookmark);

        //when
        List<BookmarkedEventSimpleInfoQuery> response = eventQueryRepository
                .findBookmarkedEventsPaging(user.getId(), PageRequest.of(0, 5));

        //then
        assertThat(response).hasSize(1)
                .extracting("status").containsExactly(BOOKMARK);
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
}