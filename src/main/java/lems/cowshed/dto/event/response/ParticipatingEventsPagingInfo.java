package lems.cowshed.dto.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.event.query.ParticipatingEventSimpleInfoQuery;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "참여한 모임 반환")
public class ParticipatingEventsPagingInfo {

    private List<ParticipatingEventSimpleInfoQuery> participatingEvents;

    @Builder
    public ParticipatingEventsPagingInfo(List<ParticipatingEventSimpleInfoQuery> participatingEvents) {
        this.participatingEvents = participatingEvents;
    }

    public static ParticipatingEventsPagingInfo from(List<ParticipatingEventSimpleInfoQuery> participatingEvents){
        return ParticipatingEventsPagingInfo.builder()
                .participatingEvents(participatingEvents)
                .build();
    }
}
