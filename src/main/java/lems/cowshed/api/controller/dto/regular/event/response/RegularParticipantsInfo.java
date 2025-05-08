package lems.cowshed.api.controller.dto.regular.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class RegularParticipantsInfo {
    @Schema(description = "정기 모임 참여 회원 정보")
    private List<RegularParticipantDetails> regularParticipants;

    @Schema(description = "정기 모임 참여한 인원 수")
    private int participantCount;

    @Builder
    private RegularParticipantsInfo(List<RegularParticipantDetails> regularParticipants, int participantCount) {
        this.regularParticipants = regularParticipants;
        this.participantCount = participantCount;
    }

    public static RegularParticipantsInfo of(List<RegularParticipantDetails> regularParticipants, int participantCount){
        return RegularParticipantsInfo.builder()
                .regularParticipants(regularParticipants)
                .participantCount(participantCount)
                .build();
    }
}
