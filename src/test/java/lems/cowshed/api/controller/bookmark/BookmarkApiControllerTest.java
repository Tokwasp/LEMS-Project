package lems.cowshed.api.controller.bookmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import lems.cowshed.service.BookmarkService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(controllers = BookmarkApiController.class)
class BookmarkApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookmarkService bookmarkService;

    @Autowired
    ObjectMapper objectMapper;

    @Disabled
    @DisplayName("회원이 등록한 북마크 폴더 목록을 찾습니다.")
    @Test
    void getAllBookmarks() throws Exception {
        //when //then
        mockMvc.perform(
                get("/bookmark")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"));
    }

}