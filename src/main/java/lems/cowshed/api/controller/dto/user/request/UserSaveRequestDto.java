package lems.cowshed.api.controller.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@NoArgsConstructor
public class UserSaveRequestDto {
    @Schema(description = "이메일", example = "test1234@naver.com")
    @NotBlank
    private String email;

    @Schema(description = "닉네임", example = "외양간")
    @NotBlank
    private String username;

    @Schema(description = "비밀번호", example = "****")
    @NotBlank
    private String password;

    @Builder
    private UserSaveRequestDto(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
