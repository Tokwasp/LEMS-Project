package lems.cowshed.api.controller.dto.user.join;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinDto {
    @Schema(description = "이메일", example = "test1234@naver.com")
    private String email;
    @Schema(description = "닉네임", example = "외양간")
    private String username;
    @Schema(description = "비밀번호", example = "****")
    private String password;
}
