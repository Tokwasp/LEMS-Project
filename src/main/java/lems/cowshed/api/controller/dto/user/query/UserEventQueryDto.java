package lems.cowshed.api.controller.dto.user.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.Gender;
import lombok.Data;

@Data
@Schema(description = "특정 모임에 속한 회원")
public class UserEventQueryDto {
    @Schema(description = "이름", example = "이길동")
    private String name;

    @Schema(description = "성별", example = "M")
    private Gender gender;

    @Schema(description = "소개", example = "성남시 분당구에 사는 직장인입니다.")
    private String introduction;
}