package lems.cowshed.api.controller.event.userevent;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.service.event.EventParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/event/{event-id}/participation")
@RequiredArgsConstructor
@RestController
public class UserEventController implements UserEventSpecification{

    private final EventParticipationService eventParticipationService;

    @PostMapping
    public CommonResponse<Void> saveEventParticipation(@PathVariable("event-id") Long eventId,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        eventParticipationService.saveEventParticipation(eventId, customUserDetails.getUserId());
        return CommonResponse.success();
    }

    @DeleteMapping
    public CommonResponse<Void> deleteEventParticipation(@PathVariable("event-id") Long eventId,
                                                         @AuthenticationPrincipal CustomUserDetails customUserDetails){
        eventParticipationService.deleteEventParticipation(eventId, customUserDetails.getUserId());
        return CommonResponse.success();
    }
}
