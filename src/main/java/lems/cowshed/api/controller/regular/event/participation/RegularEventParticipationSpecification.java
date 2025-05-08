package lems.cowshed.api.controller.regular.event.participation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.regular.event.response.RegularParticipantsInfo;
import lems.cowshed.domain.user.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name="regular-event-participation", description="정기 모임 참여 API")
public interface RegularEventParticipationSpecification {

    @Operation(summary = "정기 모임 참여", description = "정기 모임에 참여 합니다.")
    CommonResponse<Void> save(@PathVariable("regular-id") Long regularId, @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "정기 모임 참여 해제", description = "정기 모임에 참여를 해제 합니다.")
    CommonResponse<Void> delete(@PathVariable("regular-id") Long regularId, @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "정기 참여 인원 조회", description = "정기 모임에 참여한 인원들을 조회 합니다.")
    CommonResponse<RegularParticipantsInfo> getRegularParticipants(@PathVariable("regular-id") Long regularId);
}
