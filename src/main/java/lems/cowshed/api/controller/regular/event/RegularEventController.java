package lems.cowshed.api.controller.regular.event;

import jakarta.validation.Valid;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.dto.regular.event.request.RegularEventEditRequest;
import lems.cowshed.dto.regular.event.request.RegularEventSaveRequest;
import lems.cowshed.dto.regular.event.response.RegularEventPagingInfo;
import lems.cowshed.dto.regular.event.response.RegularEventSimpleInfo;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.service.regular.event.RegularEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class RegularEventController implements RegularEventSpecification {

    private final RegularEventService regularEventService;

    @PostMapping("/events/{event-id}/regular")
    public CommonResponse<Void> saveRegularEvent(@Valid @RequestBody RegularEventSaveRequest request,
                                     @PathVariable("event-id") Long eventId,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        regularEventService.saveRegularEvent(request, eventId, userDetails.getUserId());
        return CommonResponse.success();
    }

    @GetMapping("/regular/{regular-id}")
    public CommonResponse<RegularEventSimpleInfo> getRegularEvent(@PathVariable("regular-id") Long regularId) {
        RegularEventSimpleInfo info = regularEventService.getRegularEvent(regularId);
        return CommonResponse.success(info);
    }

    @GetMapping("/regular/search")
    public CommonResponse<RegularEventPagingInfo> findPagingInfo(@PageableDefault(page = 0, size = 5) Pageable pageable,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        RegularEventPagingInfo pagingInfo = regularEventService.findPagingInfo(pageable, userDetails.getUserId());
        return CommonResponse.success(pagingInfo);
    }

    @PatchMapping("/regular/{regular-id}")
    public CommonResponse<Void> editRegularEvent(@RequestBody RegularEventEditRequest request,
                                                 @PathVariable("regular-id") Long regularId) {
        regularEventService.editRegularEvent(request, regularId);
        return CommonResponse.success();
    }

    @DeleteMapping("/regular/{regular-id}")
    public CommonResponse<Void> delete(@PathVariable("regular-id") Long regularId) {
        regularEventService.delete(regularId);
        return null;
    }
}
