package lems.cowshed.api.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lems.cowshed.api.controller.dto.user.request.UserSaveRequestDto;
import lems.cowshed.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(controllers = UserApiController.class)
class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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
                post("/users/register")
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
                        post("/users/register")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].field").value("username"))
                .andExpect(jsonPath("$.error[0].message").value("유저 닉네임은 필수 입니다."));
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
                        post("/users/register")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].field").value("email"))
                .andExpect(jsonPath("$.error[0].message").value("이메일 값은 필수 입니다."));
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
                        post("/users/register")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error[0].field").value("password"))
                .andExpect(jsonPath("$.error[0].message").value("패스워드는 필수 입니다."));
    }
}