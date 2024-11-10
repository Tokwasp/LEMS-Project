package lems.cowshed.service;

import jakarta.persistence.EntityManager;
import lems.cowshed.api.controller.dto.user.request.UserEditRequestDto;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.user.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@WithMockCustomUser(id = 1L, username = "김철수",password = "1244")
class UserServiceTest {

    @Autowired EntityManager em;
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;

    //초기 데이터
    @BeforeEach
    public void init() {
        User user1 = createUser("김철수", "test2@naver.com", "1234");
        userRepository.save(user1);
    }

    @DisplayName("회원 수정 테스트")
    @Test
    public void editTest() {
        //given
        UserEditRequestDto editDto = new UserEditRequestDto(
                "김길동", "안녕하세요", "대구 광역시 수성구", LocalDate.now(), Mbti.INTP);

        //when
        userService.editProcess(editDto);
        em.flush(); em.clear();
        User user = userRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("유저가 존재 하지 않습니다."));

        //then
        assertThat(user.getUsername()).isEqualTo(editDto.getUsername());
        assertThat(user.getIntroduction()).isEqualTo(editDto.getIntroduction());
        assertThat(user.getMbti()).isEqualTo(editDto.getCharacter());
        assertThat(user.getLocation()).isEqualTo(editDto.getLocalName());
    }

    private User createUser(String username, String mail, String password) {
        return User.builder()
                .username(username)
                .email(mail)
                .password(password)
                .build();
    }
}