package lems.cowshed.domain.user.query;

import lems.cowshed.api.controller.dto.user.response.UserMyPageResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
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
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static lems.cowshed.domain.user.Mbti.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserQueryRepositoryTest {

    @Autowired
    EventJpaRepository eventJpaRepository;
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    UserEventRepository userEventRepository;

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
        List<UserEventQueryDto> userEventDto = userQueryRepository.findUserParticipatingInEvent(user.getId());

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
        List<UserEventQueryDto> userEventDto = userQueryRepository.findUserParticipatingInEvent(user.getId());

        //then
        assertThat(userEventDto).isEmpty();
    }

    @DisplayName("회원의 마이페이지 정보를 조회 한다.")
    @Test
    void findUserForMyPage() {
        //given
        Event event = createEvent("자전거 모임", "주최자");
        eventJpaRepository.save(event);
        
        User user = createUser("테스터", INTP);
        userRepository.save(user);

        Bookmark bookmark = Bookmark.create("소모임", user);
        UserEvent userEvent = UserEvent.of(user, event);
        userEventRepository.save(userEvent);

        //when
        UserMyPageResponseDto myPage = userQueryRepository.findUserForMyPage(user.getId());

        //then
        assertThat(myPage.getUserDto())
                .extracting("name", "mbti")
                .containsExactly("테스터", INTP);

        assertThat(myPage.getUserEventList().get(0))
                .extracting("eventName", "author")
                .containsExactly("자전거 모임", "주최자");

        assertThat(myPage.getBookmarkList().get(0))
                .extracting("bookmarkName")
                .isEqualTo("소모임");
    }

    private Bookmark createBookmark(String name) {
        return Bookmark.builder()
                .name(name)
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