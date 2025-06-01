package lems.cowshed.api.controller.event;

import lems.cowshed.ControllerTestSupport;
import lems.cowshed.domain.user.Role;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static lems.cowshed.domain.event.Category.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EventControllerTest extends ControllerTestSupport {

    @DisplayName("모임을 등록 합니다.")
    @Test
    void saveEvent() throws Exception {
        //when //then
        mockMvc.perform(
                post("/events")
                        .param("name", "산책 모임")
                        .param("category", String.valueOf(SPORTS))
                        .param("capacity", String.valueOf(20))
                        .param("content", "테스트")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                        .with(user(new CustomUserDetails(createForUserDetails())))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("모임을 등록 할때 최대 참가자는 100명 이다.")
    @Test
    void saveEvent_WhenOverCapacity() throws Exception {
        //when //then
        mockMvc.perform(
                        post("/events")
                                .param("name", "산책 모임")
                                .param("category", String.valueOf(SPORTS))
                                .param("capacity", String.valueOf(100))
                                .param("content", "테스트")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(csrf())
                                .with(user(new CustomUserDetails(createForUserDetails())))

                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("모임을 등록 할때 카테고리에 없는 값을 입력 한다면 예외가 발생 한다.")
    @Test
    void saveEvent_NotEnterCategory() throws Exception {
        //when //then
        mockMvc.perform(
                        post("/events")
                                .param("name", "산책 모임")
                                .param("category", "sleeping")
                                .param("capacity", String.valueOf(20))
                                .param("content", "테스트")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(csrf())
                                .with(user(new CustomUserDetails(createForUserDetails())))

                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("모임을 조회 한다.")
    @Test
    void getEvent() throws Exception {
        //given
        Long eventId = 1L;

        //when //then
        mockMvc.perform(
                get("/events/{event-id}", eventId)
                        .with(user(new CustomUserDetails(createForUserDetails())))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"));

    }

    private User createForUserDetails(){
        return User.createUserForDetails(1L, "test",
                "password", Role.ROLE_USER,"testEmail");
    }

}