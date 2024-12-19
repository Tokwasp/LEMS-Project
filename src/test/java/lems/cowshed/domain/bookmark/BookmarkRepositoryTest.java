package lems.cowshed.domain.bookmark;

import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@SpringBootTest
class BookmarkRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @DisplayName("회원과 관련된 북마크 폴더 목록을 찾습니다.")
    @Test
    void findByUserId() {
        //given
        User user = createUser();
        Bookmark bookmark = createBookmark("폴더", user);
        user.addBookmark(List.of(bookmark));
        userRepository.save(user);

        //when
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.getId());

        //then
        Assertions.assertThat(bookmarks).hasSize(1)
                .extracting("name")
                .containsExactly("폴더");
    }

    @DisplayName("회원과 관련된 북마크 폴더가 없다면 빈 목록이 반환 됩니다.")
    @Test
    void findByUserIdWhenNotExistFolder() {
        //given
        User user = createUser();
        userRepository.save(user);

        //when
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.getId());

        //then
        Assertions.assertThat(bookmarks).isEmpty();
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
}