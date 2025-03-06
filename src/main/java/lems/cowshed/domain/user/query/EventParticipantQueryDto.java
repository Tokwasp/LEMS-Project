package lems.cowshed.domain.user.query;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.Gender;
import lombok.Data;

@Data
@Schema(description = "모임에 참여한 회원 정보")
public class EventParticipantQueryDto {
    @Schema(description = "이름", example = "이길동")
    private String name;

    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    @QueryProjection
    public EventParticipantQueryDto(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }
}