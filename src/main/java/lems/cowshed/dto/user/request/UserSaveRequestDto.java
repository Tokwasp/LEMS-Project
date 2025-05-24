package lems.cowshed.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lems.cowshed.dto.Enum;
import lems.cowshed.domain.user.Gender;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.Role;
import lems.cowshed.domain.user.User;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSaveRequestDto {

    @Schema(description = "닉네임", example = "외양간")
    @NotBlank(message = "유저 닉네임은 필수 입니다.")
    @Pattern(
            regexp = "^[A-Za-z가-힣0-9]{2,10}$",
            message = "회원의 이름은 한글과 영어 문자만 가능하며 최대 10자리 입니다."
    )
    private String username;

    @Schema(description = "이메일", example = "test1234@naver.com")
    @NotBlank(message = "이메일 값은 필수 입니다.")
    private String email;

    @Schema(description = "인증 번호", example = "1352175142")
    private String code;

    @Schema(description = "비밀번호", example = "****")
    @NotBlank(message = "패스워드는 필수 입니다.")
    private String password;

    @Schema(description = "성별", example = "MALE")
    @Enum(message = "성별은 `MALE`, `FEMALE`을 허용 합니다.")
    private Gender gender;

    @Schema(description = "MBTI", example = "INTP")
    @Enum(message = "MBTI를 잘못 입력 하셨습니다.")
    private Mbti mbti;

    @Builder
    private UserSaveRequestDto(String username, String email, String code,
                               String password, Gender gender, Mbti mbti) {
        this.username = username;
        this.email = email;
        this.code = code;
        this.password = password;
        this.gender = gender;
        this.mbti = mbti;
    }

    public User toEntityForRegister(BCryptPasswordEncoder bCryptPasswordEncoder, Role role){
        return User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .email(email)
                .gender(gender)
                .role(role)
                .mbti(mbti)
                .build();
    }

}