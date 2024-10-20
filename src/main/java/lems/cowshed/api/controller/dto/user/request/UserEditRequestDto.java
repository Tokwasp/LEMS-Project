package lems.cowshed.api.controller.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.Gender;
import lems.cowshed.domain.user.Mbti;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "회원 등록")
public class UserEditRequestDto {

    @Schema(description = "닉네임", example = "외양간")
    private String username;

    @Schema(description = "소개", example = "성남시 분당구에 사는 직장인입니다.")
    private String introduction;

    @Schema(description = "지역명", example = "서울시")
    private String local_name;

    @Schema(description = "생년월일", example = "1999-05-22")
    private String birth;

    @Schema(description = "성격 유형", example = "ISTP")
    private Mbti character;

}
