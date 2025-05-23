package lems.cowshed.api.controller.regular.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.regular.event.request.RegularEventEditRequest;
import lems.cowshed.api.controller.dto.regular.event.request.RegularEventSaveRequest;
import lems.cowshed.api.controller.dto.regular.event.response.RegularEventPagingInfo;
import lems.cowshed.api.controller.dto.regular.event.response.RegularEventSimpleInfo;
import lems.cowshed.domain.user.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name="regular-event", description="정기 모임 API")
public interface RegularEventSpecification {

    @Operation(summary = "정기 모임 등록", description = "정기 모임을 등록 합니다.")
    CommonResponse<Void> saveRegularEvent(@Valid @RequestBody RegularEventSaveRequest request,
                              @PathVariable("event-id") Long eventId,
                              @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "정기 모임 조회", description = "정기 모임을 조회 합니다.")
    CommonResponse<RegularEventSimpleInfo> getRegularEvent(@PathVariable("regular-id") Long regularId);

    @Operation(summary = "정기 모임 페이징 조회", description = "정기 모임을 조회 합니다.")
    CommonResponse<RegularEventPagingInfo> findPagingInfo(Pageable pageable,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "정기 모임 수정", description = "정기 모임을 수정 합니다.")
    CommonResponse<Void> editRegularEvent(@RequestBody RegularEventEditRequest request,
                                          @PathVariable("regular-id") Long regularId);

    @Operation(summary = "정기 모임 삭제", description = "정기 모임을 삭제 합니다.")
    CommonResponse<Void> delete(@PathVariable("regular-id") Long regularId);
}