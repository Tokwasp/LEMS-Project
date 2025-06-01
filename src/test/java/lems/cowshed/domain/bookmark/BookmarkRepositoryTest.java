package lems.cowshed.domain.bookmark;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.domain.event.Event;
import lems.cowshed.repository.bookmark.BookmarkRepository;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class BookmarkRepositoryTest extends IntegrationTestSupport {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @DisplayName("회원이 등록한 북마크를 찾습니다.")
    @Test
    void findByUserId() {
        //given
        User user = createUser();
        userRepository.save(user);

        Event event = createEvent("테스트 모임");
        eventRepository.save(event);

        Bookmark bookmark = createBookmark(user.getId());
        bookmark.connectEvent(event);
        bookmarkRepository.save(bookmark);

        //when
        List<Bookmark> result = bookmarkRepository.findByUserId(user.getId());

        //then
        assertThat(result.get(0).getEvent())
                .extracting("name")
                .isEqualTo("테스트 모임");
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
        assertThat(bookmarks).isEmpty();
    }

    private Bookmark createBookmark(Long userId) {
        return Bookmark.builder()
                .userId(userId)
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