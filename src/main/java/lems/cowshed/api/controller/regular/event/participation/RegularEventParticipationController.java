package lems.cowshed.api.controller.regular.event.participation;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.service.RegularEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/regular/{regular-id}/participation")
@RequiredArgsConstructor
@RestController
public class RegularEventParticipationController implements RegularEventParticipationSpecification {

    private final RegularEventService regularEventService;

    @PostMapping
    public CommonResponse<Void> save(@PathVariable("regular-id") Long regularId,
                                     @AuthenticationPrincipal CustomUserDetails userDetails){
        regularEventService.saveParticipation(regularId,userDetails.getUserId());
        return CommonResponse.success();
    }
}
