package lems.cowshed.api.controller.regular.event.participation;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.service.RegularEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/regular")
@RequiredArgsConstructor
@RestController
public class RegularEventParticipationController implements RegularEventParticipationSpecification {

    private final RegularEventService regularEventService;

    @PostMapping("/{regular-id}/participation")
    public CommonResponse<Void> save(@PathVariable("regular-id") Long regularId,
                                     @AuthenticationPrincipal CustomUserDetails userDetails){
        regularEventService.saveParticipation(regularId,userDetails.getUserId());
        return CommonResponse.success();
    }

    @DeleteMapping("/participation/{participation-id}")
    public CommonResponse<Void> delete(@PathVariable("participation-id") Long participationId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        regularEventService.deleteParticipation(participationId, userDetails.getUserId());
        return CommonResponse.success();
    }
}
