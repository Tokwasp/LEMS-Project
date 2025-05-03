package lems.cowshed.api.controller.regular.event;

import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.recurring.event.RegularEventSaveRequest;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.service.RegularEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class RegularEventController implements RegularEventSpecification {

    private final RegularEventService regularEventService;

    @PostMapping("/events/{event-id}/regular")
    public CommonResponse<Void> save(@Valid @RequestBody RegularEventSaveRequest request,
                                     @PathVariable("event-id") Long eventId) {
        regularEventService.save(request, eventId);
        return CommonResponse.success();
    }

    @PostMapping("/regular/{regular-id}/participation")
    public CommonResponse<Void> saveParticipation(@PathVariable("regular-id") Long regularId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails){
        regularEventService.saveParticipation(regularId,userDetails.getUserId());
        return null;
    }
}
