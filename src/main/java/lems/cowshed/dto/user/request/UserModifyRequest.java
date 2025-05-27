package lems.cowshed.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lems.cowshed.domain.user.Mbti;
import lombok.*;

import java.time.LocalDate;

@Getter
@Schema(description = "회원 수정")
public class UserModifyRequest {

    @NotBlank(message = "회원 닉네임은 필수 입니다.")
    @Schema(description = "닉네임", example = "외양간")
    private String username;

    @Schema(description = "소개", example = "성남시 분당구에 사는 직장인입니다.")
    private String introduction;

    @Schema(description = "지역명", example = "서울시")
    private String location;

    @Schema(description = "생년월일", example = "1999-05-22")
    private LocalDate birth;

    @Schema(description = "성격 유형", example = "ISTP")
    private Mbti mbti;

    @Builder
    private UserModifyRequest(String username, String introduction, String location, LocalDate birth, Mbti mbti) {
        this.username = username;
        this.introduction = introduction;
        this.location = location;
        this.birth = birth;
        this.mbti = mbti;
    }

}
