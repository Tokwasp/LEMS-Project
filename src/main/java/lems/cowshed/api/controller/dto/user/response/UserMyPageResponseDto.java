package lems.cowshed.api.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.event.query.MyPageBookmarkedEventQueryDto;
import lems.cowshed.domain.event.query.MyPageParticipatingEventQueryDto;
import lems.cowshed.domain.user.query.MyPageUserQueryDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "마이 페이지 회원 정보")
public class UserMyPageResponseDto {

    @Schema(description = "유저 정보")
    private MyPageUserQueryDto userDto;

    @Schema(description = "참여 모임")
    private List<MyPageParticipatingEventQueryDto> userEventList;

    @Schema(description = "북마크 모임")
    private List<MyPageBookmarkedEventQueryDto> bookmarkList;

    public UserMyPageResponseDto(MyPageUserQueryDto userDto, List<MyPageParticipatingEventQueryDto> userEventList, List<MyPageBookmarkedEventQueryDto> bookmarkList) {
        this.userDto = userDto;
        this.userEventList = userEventList;
        this.bookmarkList = bookmarkList;
    }
}
