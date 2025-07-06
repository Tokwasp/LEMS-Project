package lems.cowshed.api.controller.user;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.dto.user.mypage.MyPageParticipatedEventsInfo;
import lems.cowshed.dto.user.mypage.MyPageInfo;
import lems.cowshed.dto.user.mypage.MyPageBookmarkedEventsInfo;
import lems.cowshed.service.user.UserMyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/my-page")
@RestController
public class UserMyPageController implements UserMyPageSpecification {

    private final UserMyPageService userMyPageService;
    private static final int PAGE_SIZE = 1;

    @GetMapping
    public CommonResponse<MyPageInfo> myPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MyPageInfo response = userMyPageService.getMyPage(userDetails.getUserId());
        return CommonResponse.success(response);
    }

    @GetMapping("/events")
    public CommonResponse<MyPageParticipatedEventsInfo> getParticipatedEvents(@RequestParam(value = "lastEventId", required = false) Long eventId,
                                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        MyPageParticipatedEventsInfo response = userMyPageService.getParticipatedEvents(eventId, userDetails.getUserId(), PAGE_SIZE);
        return CommonResponse.success(response);
    }

    @GetMapping("/bookmarks/events")
    public CommonResponse<MyPageBookmarkedEventsInfo> getBookmarkedEvents(@RequestParam(value = "lastEventId", required = false) Long participationId,
                                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        MyPageBookmarkedEventsInfo response = userMyPageService.getBookmarkedEvents(participationId, userDetails.getUserId(), PAGE_SIZE);
        return CommonResponse.success(response);
    }
}
