package lems.cowshed.api.controller.dto.event.response.query;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.Mbti;
import lombok.Getter;

@Getter
@Schema(description = "모임에 참여한 회원들 정보")
public class EventParticipantQueryDto {

    @Schema(description = "이름", example = "이길동")
    private String name;

    @Schema(description = "MBTI", example = "INTP")
    private Mbti mbti;

    @QueryProjection
    public EventParticipantQueryDto(String name, Mbti mbti) {
        this.name = name;
        this.mbti = mbti;
    }
}