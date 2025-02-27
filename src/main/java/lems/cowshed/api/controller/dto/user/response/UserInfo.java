package lems.cowshed.api.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.Mbti;
import lems.cowshed.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "회원 조회")
public class UserInfo {

    @Schema(description = "닉네임", example = "외양간")
    private String username;

    @Schema(description = "소개", example = "성남시 분당구에 사는 직장인입니다.")
    private String introduction;

    @Schema(description = "지역명", example = "서울시")
    private String localName;

    @Schema(description = "생년월일", example = "1999-05-22")
    private LocalDate birth;

    @Schema(description = "성격 유형", example = "ISTP")
    private Mbti mbti;

    @Builder
    private UserInfo(String username, String introduction, String localName, LocalDate birth, Mbti mbti) {
        this.username = username;
        this.introduction = introduction;
        this.localName = localName;
        this.birth = birth;
        this.mbti = mbti;
    }

    public static UserInfo from(User user){
        return UserInfo.builder()
                .username(user.getUsername())
                .introduction(user.getIntroduction())
                .localName(user.getLocation())
                .birth(user.getBirth())
                .mbti(user.getMbti())
                .build();
    }
}
