package lems.cowshed.api.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.api.controller.dto.user.query.UserEventMyPageQueryDto;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "마이 페이지 회원 정보")
public class UserMyPageResponseDto {

    @Schema(description = "이름", example = "하상록")
    private String name;

    @Schema(description = "생년월일", example = "1999-05-22")
    private String birth;

    @Schema(description = "성격유형", example = "ISTP")
    private String character;

    @Schema(description = "참여 모임")
    private List<UserEventMyPageQueryDto> userEventList;

    @Schema(description = "북마크 모임")
    private List<BookmarkMyPageResponseDto> bookmarkList;

}
