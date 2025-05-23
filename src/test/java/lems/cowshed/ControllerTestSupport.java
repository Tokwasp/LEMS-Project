package lems.cowshed;

import com.fasterxml.jackson.databind.ObjectMapper;
import lems.cowshed.api.controller.bookmark.BookmarkApiController;
import lems.cowshed.api.controller.event.EventController;
import lems.cowshed.api.controller.regular.event.RegularEventController;
import lems.cowshed.api.controller.user.UserApiController;
import lems.cowshed.domain.mail.code.CodeFinder;
import lems.cowshed.service.bookmark.BookmarkService;
import lems.cowshed.service.mail.MailService;
import lems.cowshed.service.regular.event.RegularEventService;
import lems.cowshed.service.event.EventService;
import lems.cowshed.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@WebMvcTest(controllers = {
        BookmarkApiController.class,
        EventController.class,
        RegularEventController.class,
        UserApiController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected EventService eventService;

    @MockBean
    protected RegularEventService regularEventService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected CodeFinder codeFinder;

    @MockBean
    protected BookmarkService bookmarkService;

    @MockBean
    protected MailService mailService;
}
