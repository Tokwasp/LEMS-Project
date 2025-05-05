package lems.cowshed.api.controller.regular.event;

import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.regular.event.RegularEventSaveRequest;
import lems.cowshed.service.RegularEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/events/{event-id}/regular")
@RequiredArgsConstructor
@RestController
public class RegularEventController implements RegularEventSpecification {

    private final RegularEventService regularEventService;

    @PostMapping
    public CommonResponse<Void> save(@Valid @RequestBody RegularEventSaveRequest request,
                                     @PathVariable("event-id") Long eventId) {
        regularEventService.save(request, eventId);
        return CommonResponse.success();
    }
}
