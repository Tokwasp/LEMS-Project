package lems.cowshed.api.controller.regular.event.participation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.domain.user.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name="regular-event-participation", description="정기 모임 참여 API")
public interface RegularEventParticipationSpecification {

    @Operation(summary = "정기 모임 참여", description = "정기 모임에 참여 합니다.")
    CommonResponse<Void> save(@PathVariable("event-id") Long eventId, @AuthenticationPrincipal CustomUserDetails userDetails);

}
