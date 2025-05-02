package lems.cowshed.api.controller.recurring.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lems.cowshed.api.controller.dto.recurring.event.RecurringEventSaveRequest;
import lems.cowshed.service.RecurringEventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(controllers = RecurringEventController.class)
class RecurringEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecurringEventService recurringEventService;

    @DisplayName("정기 모임을 등록 합니다.")
    @Test
    void saveRecurringEvent_ThenReturnsOk() throws Exception {
        //given
        RecurringEventSaveRequest request = createRecurringEventSaveRequest();

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
    void saveRecurringEvent_WhenNameIsBlank_ThenValidationFails() throws Exception {
        //given
        RecurringEventSaveRequest request =  RecurringEventSaveRequest.builder()
                .name(" ")
                .date(LocalDate.of(2025, 5, 2))
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
    void saveRecurringEvent_WhenDateIsNull_ThenValidationFails() throws Exception {
        //given
        RecurringEventSaveRequest request =  RecurringEventSaveRequest.builder()
                .name("정기 모임")
                .date(null)
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
                .andExpect(jsonPath("$.errors[0].field").value("date"))
                .andExpect(jsonPath("$.errors[0].message").value("정기 모임 일자는 필수 값 입니다."));
    }

    @DisplayName("정기 모임을 등록 할때 최대 인원은 100명 입니다.")
    @Test
    void saveRecurringEvent_WhenCapacityIsOneHundred_ThenReturnsOk() throws Exception {
        //given
        RecurringEventSaveRequest request =  RecurringEventSaveRequest.builder()
                .name("정기 모임")
                .date(LocalDate.of(2025, 5, 2))
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
    void saveRecurringEvent_WhenCapacityIsOverOneHundred_ThenValidationFails() throws Exception {
        //given
        RecurringEventSaveRequest request =  RecurringEventSaveRequest.builder()
                .name("정기 모임")
                .date(LocalDate.of(2025, 5, 2))
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

    private RecurringEventSaveRequest createRecurringEventSaveRequest() {
        return RecurringEventSaveRequest.builder()
                .name("정기 모임")
                .date(LocalDate.of(2025, 5, 2))
                .location("테스트 장소")
                .capacity(50)
                .build();
    }
}