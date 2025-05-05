package lems.cowshed.api.controller.recurring.event;

import lems.cowshed.ControllerTestSupport;
import lems.cowshed.api.controller.dto.regular.event.RegularEventSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RegularEventControllerTest extends ControllerTestSupport {

    @DisplayName("정기 모임을 등록 합니다.")
    @Test
    void save_ThenReturnsOk() throws Exception {
        //given
        RegularEventSaveRequest request = createRecurringEventSaveRequest();

        //when then
        mockMvc.perform(
                post("/events/1/regular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공"));
    }

    @DisplayName("정기 모임을 등록 할때 모임 이름은 필수 값 입니다.")
    @Test
    void save_WhenNameIsBlank_ThenValidationFails() throws Exception {
        //given
        RegularEventSaveRequest request =  RegularEventSaveRequest.builder()
                .name(" ")
                .dateTime(LocalDateTime.of(2025, 5, 2, 12, 0 ,0))
                .location("테스트 장소")
                .capacity(50)
                .build();

        //when then
        mockMvc.perform(
                        post("/events/1/regular")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("정기 모임 이름은 필수 값 입니다."));
    }

    @DisplayName("정기 모임을 등록 할때 모임 날짜는 null 값을 허용 하지 않습니다.")
    @Test
    void save_WhenDateIsNull_ThenValidationFails() throws Exception {
        //given
        RegularEventSaveRequest request =  RegularEventSaveRequest.builder()
                .name("정기 모임")
                .dateTime(null)
                .location("테스트 장소")
                .capacity(50)
                .build();

        //when then
        mockMvc.perform(
                        post("/events/1/regular")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("dateTime"))
                .andExpect(jsonPath("$.errors[0].message").value("정기 모임 일자는 필수 값 입니다."));
    }

    @DisplayName("정기 모임을 등록 할때 최대 인원은 100명 입니다.")
    @Test
    void save_WhenCapacityIsOneHundred_ThenReturnsOk() throws Exception {
        //given
        RegularEventSaveRequest request =  RegularEventSaveRequest.builder()
                .name("정기 모임")
                .dateTime(LocalDateTime.of(2025, 5, 2,12,0,0))
                .location("테스트 장소")
                .capacity(100)
                .build();

        //when then
        mockMvc.perform(
                        post("/events/1/regular")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공"));
    }

    @DisplayName("정기 모임을 등록 할때 최대 인원은 100명을 초과할 수 없습니다.")
    @Test
    void save_WhenCapacityIsOverOneHundred_ThenValidationFails() throws Exception {
        //given
        RegularEventSaveRequest request =  RegularEventSaveRequest.builder()
                .name("정기 모임")
                .dateTime(LocalDateTime.of(2025, 5, 2,12,0,0))
                .location("테스트 장소")
                .capacity(101)
                .build();

        //when then
        mockMvc.perform(
                        post("/events/1/regular")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("capacity"))
                .andExpect(jsonPath("$.errors[0].message").value("정기 모임 최대 인원은 100명 입니다."));
    }

    private RegularEventSaveRequest createRecurringEventSaveRequest() {
        return RegularEventSaveRequest.builder()
                .name("정기 모임")
                .dateTime(LocalDateTime.of(2025, 5, 2,12,0,0))
                .location("테스트 장소")
                .capacity(50)
                .build();
    }
}