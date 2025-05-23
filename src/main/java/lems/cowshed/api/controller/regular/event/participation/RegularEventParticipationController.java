package lems.cowshed.api.controller.regular.event.participation;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.dto.regular.event.response.RegularParticipantsInfo;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.service.regular.event.RegularEventParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/regular")
@RequiredArgsConstructor
@RestController
public class RegularEventParticipationController implements RegularEventParticipationSpecification {

    private final RegularEventParticipationService participationService;

    @PostMapping("/{regular-id}/participation")
    public CommonResponse<Void> save(@PathVariable("regular-id") Long regularId,
                                     @AuthenticationPrincipal CustomUserDetails userDetails){
        participationService.saveParticipation(regularId,userDetails.getUserId());
        return CommonResponse.success();
    }

    @GetMapping("/{regular-id}/participants")
    public CommonResponse<RegularParticipantsInfo> getRegularParticipants(@PathVariable("regular-id") Long regularId){
        RegularParticipantsInfo participantsInfo = participationService.getRegularParticipants(regularId);
        return CommonResponse.success(participantsInfo);
    }

    @DeleteMapping("/participation/{participation-id}")
    public CommonResponse<Void> delete(@PathVariable("participation-id") Long participationId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        participationService.deleteParticipation(participationId, userDetails.getUserId());
        return CommonResponse.success();
    }
}
