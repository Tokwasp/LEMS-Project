package lems.cowshed.api.controller.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class UserLoginRequestDto {

    @Schema(description = "이메일", example = "test1234@naver.com")
    @NotBlank
    private String email;

    @Schema(description = "비밀번호", example = "1234")
    @NotBlank
    private String password;

    @Builder
    public UserLoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
