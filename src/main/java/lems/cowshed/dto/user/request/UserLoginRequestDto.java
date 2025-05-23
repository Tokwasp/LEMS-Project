package lems.cowshed.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginRequestDto {

    @Schema(description = "이메일", example = "test1234@naver.com")
    @NotBlank(message = "이메일 값은 필수 입니다.")
    private String email;

    @Schema(description = "비밀번호", example = "1234")
    @NotBlank(message = "패스워드는 필수 입니다.")
    private String password;

    @Builder
    public UserLoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
