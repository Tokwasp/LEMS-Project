package lems.cowshed.api.controller.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper(); //JSON 변환을 위한 ObjectMapper 초기화
    }

    @Test
    void findAll() throws Exception{
        //mocking
        Pageable pageable = PageRequest.of(0, 20);

        List<EventPreviewResponseDto> results = new ArrayList<>();
        EventPreviewResponseDto dto1 = EventPreviewResponseDto.builder()
                .eventId(1L)
                .name("kim")
                .content("content 1")
                .eventDate(LocalDateTime.now())
                .capacity(100)
                .applicants(10)
                .createdDate(LocalDateTime.now())
                .build();
        EventPreviewResponseDto dto2 = EventPreviewResponseDto.builder()
                .eventId(2L)
                .name("lee")
                .content("content 2")
                .eventDate(LocalDateTime.now())
                .capacity(200)
                .applicants(20)
                .createdDate(LocalDateTime.now())
                .build();
        results.add(dto1);
        results.add(dto2);

        Slice<EventPreviewResponseDto> slice = new SliceImpl<>(results, pageable,true);

        when(eventService.findAll(null, pageable)).thenReturn((Slice<EventPreviewResponseDto>) results);

        mockMvc.perform(request(HttpMethod.GET, "/events"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.content[0].eventId").value(1L))
                .andExpect((ResultMatcher) jsonPath("$.content[1].name").value("lee"))
                .andExpect((ResultMatcher) jsonPath("$.hasNext").value(true));


    }
}