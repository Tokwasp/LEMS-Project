package lems.cowshed.domain;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.dto.user.request.UserModifyRequest;
import lems.cowshed.dto.user.request.UserSaveRequest;
import lems.cowshed.repository.bookmark.BookmarkRepository;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.repository.user.UserRepository;
import lems.cowshed.repository.event.participation.EventParticipantRepository;
import lems.cowshed.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @BeforeEach
    public void cleanUp(){
        bookmarkRepository.deleteAllInBatch();
        eventParticipantRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("신규 회원을 등록 하면 생성 날짜와 변경 날짜는 현재 날짜로 생성 된다.")
    @Test
    void JoinProcess() {
        //given
        LocalDateTime beforeSave = LocalDateTime.now();
        String email = "test@naver.com";
        UserSaveRequest request = createSaveDto(email, "테스트", "tempPassword");

        //when
        userService.signUp(request);

        //then
        LocalDateTime afterSave = LocalDateTime.now();
        User findUser = userRepository.findByEmail(email).orElseThrow();

        assertThat(findUser.getCreatedDateTime()).isBetween(beforeSave, afterSave);
        assertThat(findUser.getModifiedDateTime()).isBetween(beforeSave, afterSave);
        assertThat(findUser.getCreatedDateTime()).isEqualTo(findUser.getModifiedDateTime());
    }

    @Disabled
    @DisplayName("회원의 세부 정보를 수정 하면 변경 날짜는 생성 시간 이후 날짜이다.")
    @Test
    void editProcess() {
        //given
        LocalDateTime beforeSave = LocalDateTime.now();
        User user = createUser("테스트", "test@naver.com");
        userRepository.save(user);

        String editName = "수정한닉네임";
        UserModifyRequest request = createEditDto(editName, "안녕하세요!", Mbti.INTP);

        //when
        userService.editUser(request, user.getId());

        //then
        userRepository.flush();
        LocalDateTime afterSave = LocalDateTime.now();
        User findUser = userRepository.findByUsername(editName).orElseThrow();

        assertThat(findUser.getModifiedDateTime()).isAfter(findUser.getCreatedDateTime());
        assertThat(findUser.getCreatedDateTime()).isNotEqualTo(findUser.getModifiedDateTime());
    }

    private User createUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }

    private UserSaveRequest createSaveDto(String email, String username, String password) {
        return UserSaveRequest.builder()
                .email(email)
                .username(username)
                .password(password)
                .build();
    }

    private UserModifyRequest createEditDto(String username, String introduction, Mbti mbti) {
        return UserModifyRequest.builder()
                .username(username)
                .introduction(introduction)
                .location("대구광역시 수성구")
                .birth(LocalDate.of(2024, 11, 20))
                .mbti(mbti)
                .build();
    }
}