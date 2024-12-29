package lems.cowshed.api.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.query.UserBookmarkMyPageQueryDto;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.query.UserEventMyPageQueryDto;
import lems.cowshed.domain.user.query.UserMyPageQueryDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "마이 페이지 회원 정보")
public class UserMyPageResponseDto {

    @Schema(description = "이름", example = "하상록")
    private UserMyPageQueryDto user;

    @Schema(description = "참여 모임")
    private List<UserEventMyPageQueryDto> userEventList;

    @Schema(description = "북마크 모임")
    private List<UserBookmarkMyPageQueryDto> bookmarkList;

    public UserMyPageResponseDto(UserMyPageQueryDto user, List<UserEventMyPageQueryDto> userEventList, List<UserBookmarkMyPageQueryDto> bookmarkList) {
        this.user = user;
        this.userEventList = userEventList;
        this.bookmarkList = bookmarkList;
    }
}
