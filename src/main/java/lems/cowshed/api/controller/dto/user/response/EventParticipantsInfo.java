package lems.cowshed.api.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.api.controller.dto.user.response.query.EventParticipantQueryDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "모임에 참여한 회원정보와 참여 인원 수")
public class EventParticipantsInfo {

    @Schema(description = "모임 참여 회원 정보")
    private List<EventParticipantQueryDto> eventParticipants;

    @Schema(description = "모임에 참여한 인원 수")
    private int participantCount;

    @Builder
    private EventParticipantsInfo(List<EventParticipantQueryDto> eventParticipants, int participantCount) {
        this.eventParticipants = eventParticipants;
        this.participantCount = participantCount;
    }

    public static EventParticipantsInfo of(List<EventParticipantQueryDto> participants, int participantCount){
        return EventParticipantsInfo.builder()
                .eventParticipants(participants)
                .participantCount(participantCount)
                .build();
    }
}
