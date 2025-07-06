package lems.cowshed.api.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.domain.user.CustomUserDetails;
import lems.cowshed.dto.user.mypage.MyPageParticipatedEventsInfo;
import lems.cowshed.dto.user.mypage.MyPageInfo;
import lems.cowshed.dto.user.mypage.MyPageBookmarkedEventsInfo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "user-myPage", description = "회원 마이 페이지 API")
public interface UserMyPageSpecification {

    @Operation(summary = "회원 마이 페이지", description = "마이페이지 화면에서 회원의 간단한 정보, 가입한 모임 목록, 관심 모임 목록 수를 조회 합니다.")
    CommonResponse<MyPageInfo> myPage(@AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "참여한 모임 목록 페이징 조회",
            description = "참여한 모임들의 목록을 페이징 조회 할 수 있습니다. <br><br>" +
                    "[ 검색 방법 ] <br> " +
                    "첫 조회: eventId= 값을 넣지 않고 사용 가능. <br> " +
                    "첫 조회 이후: 마지막 모임의 eventId 값을 넣고 사용 가능")
    CommonResponse<MyPageParticipatedEventsInfo> getParticipatedEvents(@RequestParam("lastEventId") Long eventId,
                                                                       @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "북마크한 모임 목록 페이징 조회",
            description = "북마크한 모임들의 목록을 페이징 조회 할 수 있습니다. <br><br>" +
                    "검색 방법은 참여 모임 목록 페이징 조회와 같습니다.")
    CommonResponse<MyPageBookmarkedEventsInfo> getBookmarkedEvents(@RequestParam("lastEventId") Long eventId,
                                                                   @AuthenticationPrincipal CustomUserDetails userDetails);
}
