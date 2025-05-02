package lems.cowshed.api.controller.bookmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import lems.cowshed.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WithMockUser
@WebMvcTest(controllers = BookmarkApiController.class)
class BookmarkApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookmarkService bookmarkService;

    @Autowired
    ObjectMapper objectMapper;

}