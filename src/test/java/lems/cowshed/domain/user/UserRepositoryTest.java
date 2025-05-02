package lems.cowshed.domain.user;

import lems.cowshed.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class UserRepositoryTest extends IntegrationTestSupport {

    @Autowired
    UserRepository userRepository;

    @DisplayName("유저 이메일로 유저를 조회 한다.")
    @Test
    void findByEmail(){
        //given
        String targetEmail = "test@naver.com";
        User user = createUser("테스트", targetEmail);
        userRepository.save(user);

        //when
        User findUser = userRepository.findByEmail(targetEmail).orElseThrow();

        //then
        assertThat(findUser).isNotNull()
                .extracting("username", "email")
                .containsExactly("테스트", targetEmail);
    }

    @DisplayName("유저 이메일로 유저를 조회 할 때, 유저가 없을 경우에는 예외가 발생 한다.")
    @Test
    void findByEmailWhenNotFoundUser(){
        //given
        String targetEmail = "test@naver.com";
        User user = createUser("테스트", targetEmail);
        userRepository.save(user);

        //when
        //then
        assertThatThrownBy(() -> userRepository.findByEmail("noRegist@naver.com").orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("유저의 이름으로 유저를 조회 한다.")
    @Test
    void findByUsername() {
        //given
        String targetName = "테스트";
        User user = createUser(targetName, "test@naver.com");
        userRepository.save(user);

        //when
        User findUser = userRepository.findByUsername(targetName).orElseThrow();

        //then
        assertThat(findUser).isNotNull()
                .extracting("username", "email")
                .containsExactly(targetName, "test@naver.com");
    }

    @DisplayName("유저 이름으로 유저를 조회 할 때, 유저가 없을 경우에는 예외가 발생 한다.")
    @Test
    void findByNameWhenNotFoundUser(){
        //given
        String targetName = "테스트";
        User user = createUser(targetName, "test@naver.com");
        userRepository.save(user);

        //when
        //then
        assertThatThrownBy(() -> userRepository.findByUsername("등록안된유저").orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("동일한 이름 혹은 이메일을 조회 하고 있다면 true를 반환 한다")
    @CsvSource(value = {"noreg@naver.com-등록", "reg@naver.com-비등록", "reg@naver.com-등록"}, delimiter = '-')
    @ParameterizedTest
    void existsByEmailOrUsernameWhenExist(String email, String username){
        //given
        User user = createUser("등록", "reg@naver.com");
        userRepository.save(user);

        //when
        boolean result = userRepository.existsByEmailOrUsername(email, username);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("동일한 이름 혹은 이메일을 조회 한다.")
    @Test
    void existsByEmailOrUsername() {
        //given
        User user = createUser("테스트", "test@naver.com");
        userRepository.save(user);

        //when
        boolean result = userRepository.existsByEmailOrUsername("new@naver.com", "신규");

        //then
        assertThat(result).isFalse();
    }

    private User createUser(String username, String mail) {
        return User.builder()
                .username(username)
                .email(mail)
                .build();
    }
}