package lems.cowshed.api.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lems.cowshed.api.controller.dto.user.request.UserLoginRequestDto;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.service.BookmarkService;
import lems.cowshed.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(controllers = UserApiController.class)
class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BookmarkService bookmarkService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("신규 회원이 회원 가입을 합니다.")
    @Test
    void saveUser() throws Exception {

        //given
        UserSaveRequestDto request = UserSaveRequestDto.builder()
                .username("test")
                .email("test@naver.com")
                .password("tempPassword")
                .build();

        //when //then
        mockMvc.perform(
                post("/users/signUp")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공"));
    }

    @DisplayName("신규 회원이 회원 가입을 할 때 닉네임 값은 필수 입니다.")
    @Test
    void saveUserWithoutUsername() throws Exception {
        //given
        UserSaveRequestDto request = UserSaveRequestDto.builder()
                .email("test@naver.com")
                .password("tempPassword")
                .build();

        //when //then
        mockMvc.perform(
                        post("/users/signUp")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("username"))
                .andExpect(jsonPath("$.errors[0].message").value("유저 닉네임은 필수 입니다."));
    }

    @DisplayName("신규 회원이 회원 가입을 할 때 이메일 값은 필수 입니다.")
    @Test
    void saveUserWithoutEmail() throws Exception {
        //given
        UserSaveRequestDto request = UserSaveRequestDto.builder()
                .username("test")
                .password("tempPassword")
                .build();

        //when //then
        mockMvc.perform(
                        post("/users/signUp")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("이메일 값은 필수 입니다."));
    }

    @DisplayName("신규 회원이 회원 가입을 할 때 패스워드 값은 필수 입니다.")
    @Test
    void saveUserWithoutPassword() throws Exception {
        //given
        UserSaveRequestDto request = UserSaveRequestDto.builder()
                .username("test")
                .email("test@naver.com")
                .build();

        //when //then
        mockMvc.perform(
                        post("/users/signUp")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value("패스워드는 필수 입니다."));
    }

    @DisplayName("회원이 로그인 할 때 이메일 값은 빈값일 수 없습니다.")
    @Test
    void loginWhenEmailIsEmpty() throws Exception {
        //given
        UserLoginRequestDto request = UserLoginRequestDto.builder()
                .email(" ")
                .password("tempPassword")
                .build();

        //when //then
        mockMvc.perform(
                        post("/users/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("이메일 값은 필수 입니다."));
    }

    @DisplayName("회원이 로그인 할 때 비밀번호 값은 null일 수 없습니다.")
    @Test
    void loginWhenPasswordIsNull() throws Exception {
        //given
        UserLoginRequestDto request = UserLoginRequestDto.builder()
                .email("test@naver.com")
                .password(null)
                .build();

        //when //then
        mockMvc.perform(
                        post("/users/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value("패스워드는 필수 입니다."));
    }

}