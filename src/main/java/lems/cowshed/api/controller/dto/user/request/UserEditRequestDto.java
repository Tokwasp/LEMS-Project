package lems.cowshed.api.controller.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.Gender;
import lems.cowshed.domain.user.Mbti;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@Schema(description = "회원 수정")
public class UserEditRequestDto {

    @Schema(description = "닉네임", example = "외양간")
    private String username;

    @Schema(description = "소개", example = "성남시 분당구에 사는 직장인입니다.")
    private String introduction;

    @Schema(description = "지역명", example = "서울시")
    private String localName;

    @Schema(description = "생년월일", example = "1999-05-22")
    private LocalDate birth;

    @Schema(description = "성격 유형", example = "ISTP")
    private Mbti character;

    public UserEditRequestDto(){}
}
