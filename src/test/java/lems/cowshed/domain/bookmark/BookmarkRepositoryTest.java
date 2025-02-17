package lems.cowshed.domain.bookmark;

import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class BookmarkRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Disabled
    @DisplayName("회원이 등록한 북마크를 찾습니다.")
    @Test
    void findByUserId() {
        //given
        User user = createUser();
        userRepository.save(user);

        Event event = createEvent("테스트 모임");
        eventRepository.save(event);

        Bookmark bookmark = createBookmark(event, user);
        bookmarkRepository.save(bookmark);

        //when
        Bookmark result = bookmarkRepository.findByUserId(user.getId()).get(0);

        //then
        Assertions.assertThat(result.getEvent());
    }

    @DisplayName("회원이 등록한 북마크가 없다면 빈 목록이 반환 됩니다.")
    @Test
    void findByUserIdWhenNotExist() {
        //given
        User user = createUser();
        userRepository.save(user);

        //when
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.getId());

        //then
        Assertions.assertThat(bookmarks).isEmpty();
    }

    private Bookmark createBookmark(Event event, User user) {
        return Bookmark.builder()
                .event(event)
                .user(user)
                .build();
    }

    private User createUser() {
        return User.builder()
                .email("test@naver.com")
                .password("tempPassword")
                .username("테스트")
                .build();
    }

    private Event createEvent(String name) {
        return Event.builder()
                .name(name)
                .build();
    }
}