package lems.cowshed.service;

import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.user.WithMockCustomUser;
import lems.cowshed.exception.UserNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@WithMockCustomUser(id = 1L)
class UserServiceTest {

    @Autowired UserService userService;
    @Autowired UserRepository userRepository;

    @DisplayName("신규 회원을 등록 한다.")
    @Test
    void JoinProcess() {
        //given
        UserSaveRequestDto request = createSaveDto("test@naver.com", "테스트");
        
        //when
        userService.JoinProcess(request);

        //then
        User findUser = userRepository.findByUsername("테스트").orElseThrow();
        assertThat(findUser).isNotNull()
                .extracting("username", "email")
                .containsExactly("테스트", "test@naver.com");
    }

    @DisplayName("신규 회원을 등록 할 때 이미 등록된 이름, 이메일이 있을 경우 예외가 발생 한다.")
    @CsvSource(value = {"noreg@naver.com-등록", "reg@naver.com-비등록", "reg@naver.com-등록"}, delimiter = '-')
    @ParameterizedTest
    void JoinProcessWhenDuplicateNameOrEmail(String email, String username) {
        //given
        User user = createUser("reg@naver.com", "등록");
        userRepository.save(user);

        UserSaveRequestDto request = createSaveDto(email, username);

        //when //then
        assertThatThrownBy(() -> userService.JoinProcess(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("username or email already exists");
    }

    @Disabled
    @DisplayName("회원 정보를 수정 한다.")
    @Test
    void editTest() {
        //given
        User user = createUser("test@naver.com", "테스트");
        userRepository.save(user);

        LocalDate birth = LocalDate.of(1999, 3, 20);

        //when

        //then
    }

    private User createUser(String email, String username) {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }

    private UserSaveRequestDto createSaveDto(String email, String username) {
        return UserSaveRequestDto.builder()
                .email(email)
                .username(username)
                .password("tempPassword")
                .build();
    }
}