package lems.cowshed.domain.user.query;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.Gender;
import lems.cowshed.domain.user.Mbti;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "마이페이지 회원 정보")
public class MyPageUserQueryDto {

    @Schema(description = "이름", example = "하상록")
    private String name;

    @Schema(description = "생년월일", example = "1999-05-22")
    private LocalDate birth;

    @Schema(description = "성격유형", example = "ISTP")
    private Mbti mbti;

    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    @Schema(description = "지역명", example = "대구 광역시 수성구")
    private String location;

    @Schema(description = "소개", example = "안녕하세요")
    private String introduction;

    @QueryProjection
    public MyPageUserQueryDto(String name, LocalDate birth, Mbti mbti,
                              Gender gender, String location, String introduction) {
        this.name = name;
        this.birth = birth;
        this.mbti = mbti;
        this.gender = gender;
        this.location = location;
        this.introduction = introduction;
    }
}
