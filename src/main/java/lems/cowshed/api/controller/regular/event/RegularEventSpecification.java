package lems.cowshed.api.controller.regular.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.regular.event.RegularEventSaveRequest;
import lems.cowshed.domain.user.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name="regular-event", description="정기 모임 API")
public interface RegularEventSpecification {

    @Operation(summary = "정기 모임 등록", description = "정기 모임을 등록 합니다.")
    CommonResponse<Void> save(@Valid @RequestBody RegularEventSaveRequest request,
                              @PathVariable("event-id") Long eventId,
                              @AuthenticationPrincipal CustomUserDetails userDetails);
}
