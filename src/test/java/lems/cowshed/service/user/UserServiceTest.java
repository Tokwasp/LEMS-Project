package lems.cowshed.service.user;

import lems.cowshed.IntegrationTestSupport;
import lems.cowshed.dto.user.request.UserEditRequestDto;
import lems.cowshed.dto.user.request.UserLoginRequestDto;
import lems.cowshed.dto.user.request.UserSaveRequestDto;
import lems.cowshed.dto.user.response.UserMyPageInfo;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.event.participation.EventParticipantRepository;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.exception.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;

import static lems.cowshed.domain.bookmark.BookmarkStatus.BOOKMARK;
import static lems.cowshed.domain.bookmark.BookmarkStatus.NOT_BOOKMARK;
import static lems.cowshed.domain.user.Mbti.INTP;
import static org.assertj.core.api.Assertions.*;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    EventRepository eventRepository;
    @Autowired
    EventParticipantRepository eventParticipantRepository;
    @Autowired
    BookmarkRepository bookmarkRepository;

    @BeforeEach
    void cleanUp(){
        eventParticipantRepository.deleteAllInBatch();
        bookmarkRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("신규 회원 가입을 한다.")
    @Test
    void signUp() {
        //given
        String email = "test@naver.com";
        UserSaveRequestDto request = createSaveDto(email, "테스트", "tempPassword");

        //when
        userService.signUp(request);

        //then
        User findUser = userRepository.findByEmail(email).orElseThrow();
        assertThat(findUser).isNotNull()
                .extracting("username", "email")
                .containsExactly("테스트", "test@naver.com");
    }

    @DisplayName("신규 회원 가입을 할 때 이미 등록된 닉네임 혹은 이메일이 있을 경우 예외가 발생 한다.")
    @ParameterizedTest
    @CsvSource(value = {"registeredEmail@naver.com-신규 닉네임",
                        "nonRegisteredEmail@naver.com-중복 닉네임",
                        "registeredEmail@naver.com-중복 닉네임"}, delimiter = '-')
    void signUp_WhenDuplicateNameOrEmail_ThrowsException(String email, String username) {
        //given
        UserSaveRequestDto request = createSaveDto("registeredEmail@naver.com", "중복 닉네임");
        userService.signUp(request);

        UserSaveRequestDto newMember = createSaveDto(email, username);

        //when //then
        assertThatThrownBy(() -> userService.signUp(newMember))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 존재하는 닉네임 혹은 이메일 입니다.");
    }

    @DisplayName("회원이 로그인을 한다.")
    @Test
    void login() {
        //given
        String email = "test@naver.com";
        String validPassword = "validPassword";
        UserSaveRequestDto saveRequest = createSaveDto(email, "테스트", validPassword);

        userService.signUp(saveRequest);

        User user = userRepository.findByEmail(email).orElseThrow();
        UserLoginRequestDto request = createLoginDto(email, validPassword);

        //when //then
        userService.login(request);
    }

    @DisplayName("회원이 로그인을 할때 회원 가입을 안한 이메일인 경우 예외가 발생한다.")
    @Test
    void loginWhenNotFoundUserByEmail() {
        //given
        UserLoginRequestDto request = createLoginDto("NonRegisterEmail@naver.com", "tempPassword");

        //when //then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("회원을 찾지 못했습니다.");
    }

    @DisplayName("회원이 로그인 할때 비밀번호가 틀리면 예외가 발생한다.")
    @Test
    void loginWhenNotValidationPassword() {
        //given
        String email = "test@naver.com";
        UserSaveRequestDto saveRequest = createSaveDto(email, "테스트", "validPassword");
        userService.signUp(saveRequest);

        User user = userRepository.findByEmail(email).orElseThrow();
        UserLoginRequestDto request = createLoginDto(user.getEmail(), "notValidPassword");

        //when //then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("아이디와 비밀번호를 다시 확인 해주세요");
    }

    @DisplayName("회원의 세부 정보를 수정 한다.")
    @Test
    void editProcess() {
        //given
        User user = createUser("테스트", "test@naver.com");
        userRepository.save(user);

        String editName = "수정한닉네임";
        UserEditRequestDto request = createEditDto(editName, "안녕하세요!", Mbti.INTP);

        //when
        userService.editUser(request, user.getId(), user.getUsername());

        //then
        User findUser = userRepository.findByUsername(editName).orElseThrow();
        assertThat(findUser).extracting("username", "introduction", "mbti")
                .containsExactly(editName, "안녕하세요!", Mbti.INTP);
    }

    @DisplayName("회원의 세부 정보를 수정 할때 같은 닉네임을 가진 회원이 있다면 예외가 발생 한다.")
    @Test
    void editProcessWhenDuplicateUsername() {
        //given
        User user = createUser("테스터", "test@naver.com");
        userRepository.save(user);

        User user2 = createUser("등록된 닉네임", "new@naver.com");
        userRepository.save(user2);

        UserEditRequestDto request = createEditDto("등록된 닉네임", "안녕하세요!", Mbti.INTP);

        //when //then
        assertThatThrownBy(() -> userService.editUser(request, user.getId(), user.getUsername()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 존재하는 닉네임 입니다.");
    }

    @DisplayName("회원의 세부 정보를 수정 할때 조회할 회원의 식별자 값이 저장소에 없다면 예외가 발생 한다")
    @Test
    void editProcessWhenNotFoundUserById() {
        //given
        User user = createUser("test", "test@naver.com");
        userRepository.save(user);

        UserEditRequestDto request = createEditDto("새닉네임", "안녕하세요!", Mbti.INTP);

        //when //then
        assertThatThrownBy(() -> userService.editUser(request, 2L, user.getUsername()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("회원을 찾지 못했습니다.");
    }

    @DisplayName("회원의 비밀번호를 변경 한다.")
    @Test
    void modifyPassword() {
        //given
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        User user = createUser("test", "test@naver.com", oldPassword);
        userRepository.save(user);

        //when
        userService.modifyPassword(user, newPassword);

        //then
        User findUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertThat(bCryptPasswordEncoder.matches(newPassword, findUser.getPassword())).isTrue();
    }

    @DisplayName("회원의 마이 페이지 조회 할 때 북마크 되지 않은 모임은 북마크 되지 않음을 표기 한다.")
    @Test
    void findMyPage() {
        //given
        Event event = createEvent("자전거 모임", "주최자");
        Event event2 = createEvent("테스트 모임", "테스터");
        eventRepository.saveAll(List.of(event, event2));

        User user = createUser("테스터", INTP);
        userRepository.save(user);

        EventParticipation eventParticipation = EventParticipation.of(user, event);
        EventParticipation eventParticipation2 = EventParticipation.of(user, event2);
        eventParticipantRepository.saveAll(List.of(eventParticipation, eventParticipation2));

        Bookmark bookmark = createBookmark(event, user);
        bookmarkRepository.save(bookmark);

        //when
        UserMyPageInfo result = userService.findMyPage(user.getId());

        //then
        assertThat(result.getUserEventList())
                .extracting("bookmarkStatus")
                .containsExactlyInAnyOrder(BOOKMARK, NOT_BOOKMARK);
    }

    private User createUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }

    private User createUser(String username, String email, String password) {
        return User.builder()
                .username(username)
                .email(email)
                .password((password))
                .build();
    }

    private UserSaveRequestDto createSaveDto(String email, String username, String password) {
        return UserSaveRequestDto.builder()
                .email(email)
                .username(username)
                .password(password)
                .build();
    }

    private UserSaveRequestDto createSaveDto(String email, String username) {
        return UserSaveRequestDto.builder()
                .email(email)
                .username(username)
                .password("tempPassword")
                .build();
    }

    private UserLoginRequestDto createLoginDto(String email, String password){
        return UserLoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
    }

    private UserEditRequestDto createEditDto(String username, String introduction, Mbti mbti) {
        return UserEditRequestDto.builder()
                .username(username)
                .introduction(introduction)
                .localName("대구광역시 수성구")
                .birth(LocalDate.of(2024, 11, 20))
                .mbti(mbti)
                .build();
    }

    private User createUser(String username, Mbti mbti) {
        return User.builder()
                .username(username)
                .mbti(mbti)
                .build();
    }

    private Event createEvent(String name, String author) {
        return Event.builder()
                .name(name)
                .author(author)
                .build();
    }

    private Bookmark createBookmark(Event event, User user) {
        return Bookmark.builder()
                .event(event)
                .user(user)
                .status(BOOKMARK)
                .build();
    }
}