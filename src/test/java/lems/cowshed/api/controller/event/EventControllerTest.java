package lems.cowshed.api.controller.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.response.EventSimpleInfo;
import lems.cowshed.api.controller.dto.event.response.EventsSearchInfo;
import lems.cowshed.domain.event.Category;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.user.Role;
import lems.cowshed.domain.user.User;
import lems.cowshed.service.CustomUserDetails;
import lems.cowshed.service.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static lems.cowshed.domain.event.Category.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("모임을 등록 합니다.")
    @Test
    void saveEvent() throws Exception {
        //given
        EventSaveRequestDto request = createEventSaveRequest("산책 모임", SPORTS, 20);

        //when //then
        mockMvc.perform(
                post("/events")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user(new CustomUserDetails(createForUserDetails())))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공"));
    }

    @DisplayName("모임을 등록 할때 최대 참가자는 200명을 초과 할수 없습니다.")
    @Test
    void saveEventWhenOverCapacity() throws Exception {
        //given
        EventSaveRequestDto request = createEventSaveRequest("산책 모임", SPORTS, 201);

        //when //then
        mockMvc.perform(
                        post("/events")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("capacity"));
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

    @DisplayName("키워드로 모임을 검색 한다.")
    @Test
    void searchEventsByNameOrContent() throws Exception {
        //given
        String keyword = "모임";
        Long userId = 1L;

        Event event = createEvent("테스터", "모임", "내용");
        List<EventSimpleInfo> eventSimpleInfoList = List.of(EventSimpleInfo.of(event, 2L, BOOKMARK));
        EventsSearchInfo mockResult = new EventsSearchInfo(eventSimpleInfoList);

        when(eventService.searchEventsByNameOrContent(keyword, userId)).thenReturn(mockResult);

        //when //then
        mockMvc.perform(
                        get("/events/search")
                                .param("keyword", keyword)
                                .with(user(new CustomUserDetails(createForUserDetails())))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.searchResults[0].name").value("모임"))
                .andExpect(jsonPath("$.data.searchResults[0].applicants").value(2L))
                .andExpect(jsonPath("$.data.searchResults[0].bookmarkStatus").value("BOOKMARK"));
    }

    private EventSaveRequestDto createEventSaveRequest(String name, Category category, int capacity) {
        return EventSaveRequestDto.builder()
                .name(name)
                .eventDate(LocalDate.of(2025,1,1))
                .content("산책 하실분 모집 합니다.")
                .category(category)
                .capacity(capacity)
                .location("한라산")
                .build();
    }

    private static Event createEvent(String author, String name, String content){
        return Event.builder()
                .name(name)
                .email("test@naver.com")
                .author(author)
                .content(content)
                .build();
    }

    private User createForUserDetails(){
        return User.createUserForDetails(1L, "test",
                "password", Role.ROLE_USER,"testEmail");
    }

}