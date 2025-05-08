package lems.cowshed.api.controller.dto.regular.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.user.Mbti;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "정기 모임 참여한 회원들 정보")
public class RegularParticipantDetails {

    @Schema(description = "이름", example = "이길동")
    private String name;

    @Schema(description = "MBTI", example = "INTP")
    private Mbti mbti;

    @Builder
    private RegularParticipantDetails(String name, Mbti mbti) {
        this.name = name;
        this.mbti = mbti;
    }

    public static RegularParticipantDetails of(String name, Mbti mbti){
        return RegularParticipantDetails.builder()
                .name(name)
                .mbti(mbti)
                .build();
    }
}