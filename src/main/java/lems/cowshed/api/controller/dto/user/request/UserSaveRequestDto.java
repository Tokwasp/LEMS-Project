package lems.cowshed.api.controller.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lems.cowshed.domain.user.Gender;
import lems.cowshed.domain.user.Role;
import lems.cowshed.domain.user.User;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSaveRequestDto {

    @Schema(description = "이메일", example = "test1234@naver.com")
    @NotBlank(message = "이메일 값은 필수 입니다.")
    private String email;

    @Schema(description = "닉네임", example = "외양간")
    @NotBlank(message = "유저 닉네임은 필수 입니다.")
    private String username;

    @Schema(description = "비밀번호", example = "****")
    @NotBlank(message = "패스워드는 필수 입니다.")
    private String password;

    @Schema(description = "성별", example = "남성")
    private Gender gender;

    @Builder
    private UserSaveRequestDto(String email, String username, String password, Gender gender) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.gender = gender;
    }

    public User toEntityForRegister(BCryptPasswordEncoder bCryptPasswordEncoder, Role role){
        return User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .email(email)
                .gender(gender)
                .role(role)
                .build();
    }
}