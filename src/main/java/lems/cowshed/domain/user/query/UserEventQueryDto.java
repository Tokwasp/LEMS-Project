package lems.cowshed.domain.user.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.Gender;
import lems.cowshed.domain.user.Mbti;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserEventQueryDto {
    @Schema(description = "이름", example = "이길동")
    private String name;

    @Schema(description = "성별", example = "M")
    private Gender gender;

    @Schema(description = "mbti", example = "INTP")
    private Mbti mbti;

    @Schema(description = "나이", example = "26")
    private int age;

    @JsonIgnore
    @Schema(description = "생일", example = "2020-01-01")
    private LocalDate birth;

    @Schema(description = "지역", example = "대구 광역시 수성구")
    private String location;

    @QueryProjection
    public UserEventQueryDto(String name, Gender gender, Mbti mbti, LocalDate birth, String location) {
        this.name = name;
        this.gender = gender;
        this.mbti = mbti;
        this.birth = birth;
        this.location = location;
    }
}