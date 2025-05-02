package lems.cowshed.api.controller.recurring.event;

import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.recurring.event.RecurringEventSaveRequest;
import lems.cowshed.service.RecurringEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/recurring-event")
@RequiredArgsConstructor
@RestController
public class RecurringEventController implements RecurringEventSpecification {

    private final RecurringEventService recurringEventService;

    @PostMapping
    public CommonResponse<Void> saveRecurringEvent(@Valid @RequestBody RecurringEventSaveRequest request) {
        recurringEventService.save(request);
        return CommonResponse.success();
    }
}
