package lems.cowshed.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.dto.Enum;
import lems.cowshed.domain.user.Gender;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.Role;
import lems.cowshed.domain.user.User;
import lems.cowshed.global.validator.EmailCheck;
import lems.cowshed.global.validator.NameCheck;
import lems.cowshed.global.validator.PasswordCheck;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSaveRequest {

    @Schema(description = "닉네임", example = "외양간")
    @NameCheck
    private String username;

    @Schema(description = "이메일", example = "test1234@naver.com")
    @EmailCheck
    private String email;

    @Schema(description = "인증 번호", example = "1352175142")
    private String code;

    @Schema(description = "비밀번호", example = "****")
    @PasswordCheck
    private String password;

    @Schema(description = "성별", example = "MALE")
    @Enum(message = "성별은 `MALE`, `FEMALE`을 허용 합니다.")
    private Gender gender;

    @Schema(description = "MBTI", example = "INTP")
    @Enum(message = "MBTI를 잘못 입력 하셨습니다.")
    private Mbti mbti;

    @Builder
    private UserSaveRequest(String username, String email, String code,
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