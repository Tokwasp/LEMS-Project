package lems.cowshed.api.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.event.query.BookmarkedEventSimpleInfoQuery;
import lems.cowshed.domain.event.query.ParticipatingEventSimpleInfoQuery;
import lems.cowshed.domain.user.query.MyPageUserQueryDto;
import lombok.AccessLevel;
import lombok.Builder;
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
    private List<ParticipatingEventSimpleInfoQuery> userEventList;

    @Schema(description = "북마크 모임")
    private List<BookmarkedEventSimpleInfoQuery> bookmarkList;

    @Builder
    public UserMyPageResponseDto(MyPageUserQueryDto userDto, List<ParticipatingEventSimpleInfoQuery> userEventList, List<BookmarkedEventSimpleInfoQuery> bookmarkList) {
        this.userDto = userDto;
        this.userEventList = userEventList;
        this.bookmarkList = bookmarkList;
    }

    public static UserMyPageResponseDto of(MyPageUserQueryDto userDto,
                                           List<ParticipatingEventSimpleInfoQuery> userEventList,
                                           List<BookmarkedEventSimpleInfoQuery> bookmarkList){
        return UserMyPageResponseDto.builder()
                .userDto(userDto)
                .userEventList(userEventList)
                .bookmarkList(bookmarkList)
                .build();
    }
}
