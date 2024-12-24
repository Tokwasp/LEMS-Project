package lems.cowshed.service;

import lems.cowshed.api.controller.dto.bookmark.request.BookmarkEditRequestDto;
import lems.cowshed.api.controller.dto.bookmark.request.BookmarkSaveRequestDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class BookmarkServiceTest {

    @Autowired
    BookmarkService bookmarkService;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Autowired
    UserRepository userRepository;

    @DisplayName("북마크 이름을 통해 북마크 폴더를 만든다.")
    @Test
    void createBookmark() {
        //given
        User user = createUser();
        userRepository.save(user);
        BookmarkSaveRequestDto request = BookmarkSaveRequestDto.of("폴더");

        //when
        bookmarkService.createBookmark(user.getId(), request);

        //then
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.getId());
        assertThat(bookmarks).hasSize(1)
                .extracting("name")
                .containsExactly("폴더");
    }

    @DisplayName("회원의 북마크 폴더 이름을 모두 찾는다.")
    @Test
    void getAllBookmarks() {
        //given
        User user = createUser();
        Bookmark bookmark1 = createBookmark("운동", user);
        Bookmark bookmark2 = createBookmark("산책", user);
        Bookmark bookmark3 = createBookmark("수영", user);
        user.addBookmark(List.of(bookmark1, bookmark2, bookmark3));
        userRepository.save(user);

        //when
        List<String> bookmarkFolderNames = bookmarkService.getAllBookmarks(user.getId()).getBookmarkFolderNames();

        //then
        assertThat(bookmarkFolderNames).hasSize(3)
                .containsExactlyInAnyOrder("운동", "산책", "수영");
    }

    @DisplayName("새로운 폴더명을 받아 기존 북마크 폴더의 폴더명을 수정 합니다.")
    @Test
    void test() {
        //given
        String oldFolderName = "폴더";
        String newFolderName = "새폴더";

        User user = createUser();
        Bookmark bookmark = createBookmark(oldFolderName, user);
        user.addBookmark(List.of(bookmark));
        userRepository.save(user);
        BookmarkEditRequestDto request = createEditRequest(bookmark, newFolderName);

        //when
        bookmarkService.editBookmarkName(request, bookmark.getId());

        //then
        Bookmark findBookmark = bookmarkRepository.findById(bookmark.getId()).orElseThrow(
                () -> new IllegalArgumentException("찾을수 없는 북마크 폴더 입니다.")
        );
        assertThat(findBookmark).isNotNull()
                .extracting("name")
                .isEqualTo(newFolderName);
    }

    private Bookmark createBookmark(String folderName, User user) {
        return Bookmark.builder()
                .name(folderName)
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

    private BookmarkEditRequestDto createEditRequest(Bookmark bookmark, String folderName) {
        return BookmarkEditRequestDto.builder()
                .bookmarkId(bookmark.getId())
                .newBookmarkFolderName(folderName)
                .build();
    }
}