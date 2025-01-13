package lems.cowshed.api.controller.userevent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.config.swagger.ApiErrorCodeExamples;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserEventSpecification {

    @Operation(summary = "모임 참여", description = "회원이 모임에 참여 합니다.")
    @ApiErrorCodeExamples({ErrorCode.SUCCESS, ErrorCode.NOT_FOUND_ERROR})
    CommonResponse<Void> saveUserEvent(@Parameter(name="event-id", description = "모임 id", example = "1") @PathVariable Long eventId);

}
