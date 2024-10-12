package lems.cowshed.api.controller.dto.user.join;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.event.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Schema(description = "로그인 결과")
@Getter
@Setter
public class JoinResult {

    @Schema(description = "결과", example = "회원 가입에 성공 했습니다.")
    private String message;

    public JoinResult(String message) {
        this.message = message;
    }
}
