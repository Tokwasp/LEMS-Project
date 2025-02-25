package lems.cowshed.api.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.user.query.UserBookmarkMyPageQueryDto;
import lems.cowshed.domain.user.query.UserEventMyPageQueryDto;
import lems.cowshed.domain.user.query.UserMyPageQueryDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "마이 페이지 회원 정보")
public class UserMyPageResponseDto {

    @Schema(description = "유저 정보")
    private UserMyPageQueryDto userDto;

    @Schema(description = "참여 모임")
    private List<UserEventMyPageQueryDto> userEventList;

    @Schema(description = "북마크 모임")
    private List<UserBookmarkMyPageQueryDto> bookmarkList;

    public UserMyPageResponseDto(UserMyPageQueryDto userDto, List<UserEventMyPageQueryDto> userEventList, List<UserBookmarkMyPageQueryDto> bookmarkList) {
        this.userDto = userDto;
        this.userEventList = userEventList;
        this.bookmarkList = bookmarkList;
    }
}
