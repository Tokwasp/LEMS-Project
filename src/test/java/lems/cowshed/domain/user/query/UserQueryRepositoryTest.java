package lems.cowshed.domain.user.query;

import lems.cowshed.api.controller.dto.user.response.UserMyPageResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventJpaRepository;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.userevent.UserEvent;
import lems.cowshed.domain.userevent.UserEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static lems.cowshed.domain.user.Mbti.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserQueryRepositoryTest {

    @Autowired
    EventJpaRepository eventJpaRepository;
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    UserEventRepository userEventRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @DisplayName("모임에 참여한 회원을 조회 한다.")
    @Test
    void findUserParticipatingInEvent() {
        //given
        User user = createUser("테스터", INTP);
        Event event = createEvent("산책 모임", "산책회");
        UserEvent userEvent = UserEvent.of(user, event);
        eventJpaRepository.save(event);
        userRepository.save(user);
        userEventRepository.save(userEvent);

        //when
        List<EventParticipantQueryDto> userEventDto = userQueryRepository.findUserParticipatingInEvent(user.getId());

        //then
        assertThat(userEventDto.get(0))
                .extracting("name", "mbti")
                .containsExactly("테스터", INTP);
    }

    @DisplayName("모임에 참여한 회원을 조회 할때 참여한 회원이 없다면 빈 리스트를 반환 한다.")
    @Test
    void findUserParticipatingInEventWhenZeroParticipating() {
        //given
        User user = createUser("테스터", INTP);
        Event event = createEvent("산책 모임", "산책회");
        eventJpaRepository.save(event);
        userRepository.save(user);

        //when
        List<EventParticipantQueryDto> userEventDto = userQueryRepository.findUserParticipatingInEvent(user.getId());

        //then
        assertThat(userEventDto).isEmpty();
    }

    @DisplayName("한명의 회원이 2개의 모임 참여, 2개의 북마크 했을때 마이 페이지 정보를 확인 한다.")
    @Test
    void findUserForMyPageWhenTwoParticipateEventAndTwoBookmark() {
        //given
        Event event = createEvent("자전거 모임", "주최자");
        Event event2 = createEvent("테스트 모임", "테스터");
        eventJpaRepository.saveAll(List.of(event, event2));
        
        User user = createUser("테스터", INTP);
        userRepository.save(user);

        Bookmark bookmark = createBookmark(event, user);
        Bookmark bookmark2 = createBookmark(event2, user);
        bookmarkRepository.saveAll(List.of(bookmark,bookmark2));

        UserEvent userEvent = UserEvent.of(user, event);
        UserEvent userEvent2 = UserEvent.of(user, event2);
        userEventRepository.saveAll(List.of(userEvent, userEvent2));

        //when
        UserMyPageResponseDto myPage = userQueryRepository.findUserForMyPage(user.getId(), List.of(event.getId(), event2.getId()));

        //then
        assertThat(myPage.getUserDto())
                .extracting("name", "mbti")
                .containsExactly("테스터", INTP);

        assertThat(myPage.getUserEventList()).hasSize(2)
                .extracting("eventName")
                .containsExactlyInAnyOrder("자전거 모임", "테스트 모임");

        assertThat(myPage.getBookmarkList()).hasSize(2)
                .extracting("author")
                .containsExactlyInAnyOrder("주최자", "테스터");
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