package lems.cowshed.api.controller.event.participation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.config.swagger.ApiErrorCodeExamples;
import lems.cowshed.domain.user.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name="event-participation", description="모임 참여 API")
public interface EventParticipationSpecification {

    @Operation(summary = "모임 참여", description = "회원이 모임에 참여 합니다.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> saveEventParticipation(@Parameter(name="event-id", description = "모임 id", example = "1") @PathVariable Long eventId
            , @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "모임 참여 제거", description = "회원의 모임 참석을 제거 합니다.")
    @ApiErrorCodeExamples({ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> deleteEventParticipation(@Parameter(name="event-id", description = "모임 id", example = "1") @PathVariable Long eventId
            , @AuthenticationPrincipal CustomUserDetails customUserDetails);

}
