package lems.cowshed.api.controller.user;

import lems.cowshed.ControllerTestSupport;
import lems.cowshed.dto.user.request.UserLoginRequest;
import lems.cowshed.dto.user.request.UserSaveRequest;
import lems.cowshed.domain.user.Mbti;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static lems.cowshed.domain.user.Gender.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserApiControllerTest extends ControllerTestSupport {

    @DisplayName("신규 회원이 회원 가입을 합니다.")
    @Test
    void saveUser() throws Exception {
        //given
        UserSaveRequest request = createRequestDto();

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
        UserSaveRequest request = UserSaveRequest.builder()
                .email("test@naver.com")
                .password("tempPassword!@")
                .gender(MALE)
                .mbti(Mbti.INTP)
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
        UserSaveRequest request = UserSaveRequest.builder()
                .username("test")
                .password("tempPassword!@")
                .gender(MALE)
                .mbti(Mbti.INTP)
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
        UserSaveRequest request = UserSaveRequest.builder()
                .username("test")
                .email("test@naver.com")
                .gender(MALE)
                .mbti(Mbti.INTP)
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
                .andExpect(jsonPath("$.errors[0].message").value("회원 비밀번호는 필수 입니다."));
    }

    @DisplayName("신규 회원이 회원 가입을 할 때 성별 값은 필수 입니다.")
    @Test
    void saveUserWithoutGender() throws Exception {
        //given
        UserSaveRequest request = UserSaveRequest.builder()
                .username("test")
                .password("tempPassword!@")
                .email("test@naver.com")
                .mbti(Mbti.INTP)
                .build();

        //when //then
        mockMvc.perform(
                post("/users/signUp")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("gender"))
                .andExpect(jsonPath("$.errors[0].message").value("성별은 `MALE`, `FEMALE`을 허용 합니다."));
    }

    @DisplayName("회원이 로그인 할 때 이메일 값은 빈값일 수 없습니다.")
    @Test
    void loginWhenEmailIsEmpty() throws Exception {
        //given
        UserLoginRequest request = UserLoginRequest.builder()
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
        UserLoginRequest request = UserLoginRequest.builder()
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

    private UserSaveRequest createRequestDto(){
        return UserSaveRequest.builder()
                .username("test")
                .email("test@naver.com")
                .code("1234")
                .password("tempPassword!@")
                .gender(MALE)
                .mbti(Mbti.INFJ)
                .build();
    }
}