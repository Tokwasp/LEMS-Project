package lems.cowshed.service;

import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class BookmarkServiceTest {

    @Autowired
    BookmarkService bookmarkService;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @DisplayName("회원이 모임을 북마크 등록 합니다.")
    @Test
    void saveBookmark() {
        //given
        User user = createUser();
        userRepository.save(user);

        Event event = createEvent("테스트 모임", "테스트");
        eventRepository.save(event);

        //when
        Long bookmarkId = bookmarkService.saveBookmark(event.getId(), user.getId());

        //then
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElseThrow();
        assertThat(bookmark.getEvent())
                .extracting("name", "content")
                .containsExactly("테스트 모임", "테스트");
    }

    @DisplayName("회원의 북마크를 모두 찾습니다.")
    @Test
    void getAllBookmarks() {
        //given
        User user = createUser();
        userRepository.save(user);

        Event event1 = createEvent("테스트 모임1", "테스트1");
        Event event2 = createEvent("테스트 모임2", "테스트2");
        Event event3 = createEvent("테스트 모임3", "테스트3");
        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);

        Bookmark bookmark1 = createBookmark(event1, user);
        Bookmark bookmark2 = Bookmark.create(event2, user);
        Bookmark bookmark3 = Bookmark.create(event3, user);
        bookmarkRepository.save(bookmark1);
        bookmarkRepository.save(bookmark2);
        bookmarkRepository.save(bookmark3);

        //when
        List<EventPreviewResponseDto> result = bookmarkService.getAllBookmarks(user.getId()).getBookmarks();

        //then
        assertThat(result).hasSize(3)
                .extracting("content")
                .containsExactlyInAnyOrder("테스트1", "테스트2", "테스트3");
    }

    @DisplayName("북마크를 제거 합니다.")
    @Test
    void deleteBookmark() {
        //given
        User user = createUser();
        userRepository.save(user);

        Event event = createEvent("테스트 모임", "테스트");
        eventRepository.save(event);

        Bookmark bookmark = createBookmark(event, user);
        bookmarkRepository.save(bookmark);

        //when
        bookmarkService.deleteBookmark(bookmark.getId());

        //then
        assertThatThrownBy(() -> bookmarkRepository.findById(bookmark.getId()).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
    }

    private User createUser() {
        return User.builder()
                .email("test@naver.com")
                .password("tempPassword")
                .username("테스트")
                .build();
    }

    private Bookmark createBookmark(Event event, User user) {
        return Bookmark.builder()
                .event(event)
                .user(user)
                .build();
    }

    private Event createEvent(String name, String content){
        return Event.builder()
                .name(name)
                .content(content)
                .build();
    }

}