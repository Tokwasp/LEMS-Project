package lems.cowshed.api.controller.recurring.event;

import io.swagger.v3.oas.annotations.Operation;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.recurring.event.RecurringEventSaveRequest;
import org.springframework.web.bind.annotation.RequestBody;

public interface RecurringEventSpecification {

    @Operation(summary = "정기 모임 등록", description = "정기 모임을 등록 합니다.")
    CommonResponse<Void> saveRecurringEvent(@RequestBody RecurringEventSaveRequest request);

}
