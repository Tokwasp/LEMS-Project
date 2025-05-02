package lems.cowshed.api.controller.bookmark;

import io.swagger.v3.oas.annotations.Operation;
import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.ErrorCode;
import lems.cowshed.config.swagger.ApiErrorCodeExample;
import lems.cowshed.domain.user.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

public interface BookmarkSpecification {
    @Operation(summary = "북마크 추가", description = "회원이 모임을 북마크 합니다.")
    @ApiErrorCodeExample(ErrorCode.NOT_FOUND_ERROR)
    CommonResponse<Void> saveBookmark(@PathVariable("event-id") Long eventId,
                                      @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "북마크 삭제", description = "회원이 모임 북마크 삭제 합니다.")
    CommonResponse<Void> deleteBookmark(@PathVariable("id") long eventId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails);
}
