package lems.cowshed.api.controller.userevent;

import lems.cowshed.api.controller.CommonResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-event")
public class UserEventController implements UserEventSpecification{

    @PostMapping("/{event-id}")
    public CommonResponse<Void> saveUserEvent(@PathVariable Long eventId) {
        return null;
    }
}
