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

    @Schema(description = "최대 인원 수")
    private int capacity;

    @Builder
    private RegularParticipantsInfo(List<RegularParticipantDetails> regularParticipants, int participantCount, int capacity) {
        this.regularParticipants = regularParticipants;
        this.participantCount = participantCount;
        this.capacity = capacity;
    }

    public static RegularParticipantsInfo of(List<RegularParticipantDetails> regularParticipants, int participantCount, int capacity){
        return RegularParticipantsInfo.builder()
                .regularParticipants(regularParticipants)
                .participantCount(participantCount)
                .capacity(capacity)
                .build();
    }
}
