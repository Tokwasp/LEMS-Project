package lems.cowshed.dto.regular.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class RegularEventSearchResponse {

    @Schema(description = "검색 결과")
    private List<RegularEventSearchInfo> searchInfos;

    @Schema(description = "다음 페이지 여부")
    private boolean hasNext;

    private RegularEventSearchResponse(List<RegularEventSearchInfo> searchInfos, boolean hasNext) {
        this.searchInfos = searchInfos;
        this.hasNext = hasNext;
    }

    public static RegularEventSearchResponse of(List<RegularEvent> regularEvents, Map<Long, List<RegularEventParticipation>> groupedRegularEventIdMap,
                                                List<EventParticipation> participants, Long userId, boolean hasNext) {

        Map<Long, Set<Long>> eventIdParticipantUserIdsMap = groupEventIdParticipantIds(participants);

        List<RegularEventSearchInfo> searchInfos = regularEvents.stream()
                .map(regularEvent -> {
                    List<RegularEventParticipation> regularParticipants = groupedRegularEventIdMap.getOrDefault(regularEvent.getId(), Collections.emptyList());

                    return RegularEventSearchInfo.of(
                            regularEvent,
                            regularParticipants.size(),
                            eventIdParticipantUserIdsMap.getOrDefault(regularEvent.getEvent().getId(), Collections.emptySet()).contains(userId)
                    );
                })
                .toList();

        return new RegularEventSearchResponse(searchInfos, hasNext);
    }

    private static Map<Long, Set<Long>> groupEventIdParticipantIds(List<EventParticipation> participants) {
        return participants.stream()
                .collect(Collectors.groupingBy(
                        EventParticipation::getEventId,
                        Collectors.mapping(
                                participant -> participant.getUser().getId(),
                                Collectors.toSet()
                        )
                ));
    }
}
