package lems.cowshed.service;

import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
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
        assertThat(bookmark.getStatus()).isEqualTo(BOOKMARK);
        assertThat(bookmark.getEvent())
                .extracting("name", "content")
                .containsExactly("테스트 모임", "테스트");
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
        bookmarkService.deleteBookmark(event.getId(), user.getId());

        //then
        Bookmark findBookmark = bookmarkRepository.findById(bookmark.getId()).orElseThrow();
        assertThat(findBookmark.getStatus()).isEqualTo(DELETE);
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
                .status(BOOKMARK)
                .build();
    }

    private Event createEvent(String name, String content){
        return Event.builder()
                .name(name)
                .content(content)
                .build();
    }

}