package lems.cowshed.api.controller.recurring.event;

import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.recurring.event.RecurringEventSaveRequest;
import lems.cowshed.service.RecurringEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/events/{event-id}/regular")
@RequiredArgsConstructor
@RestController
public class RecurringEventController implements RecurringEventSpecification {

    private final RecurringEventService recurringEventService;

    @PostMapping
    public CommonResponse<Void> saveRecurringEvent(@Valid @RequestBody RecurringEventSaveRequest request,
                                                   @PathVariable("event-id") Long eventId) {
        recurringEventService.save(request, eventId);
        return CommonResponse.success();
    }
}
