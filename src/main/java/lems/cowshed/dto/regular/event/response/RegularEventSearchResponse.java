package lems.cowshed.dto.regular.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.regular.event.RegularEvent;
import lombok.Getter;

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

    public static RegularEventSearchResponse of(List<RegularEvent> regularEventFetchEvent, List<RegularEvent> regularFetchParticipation,
                                                List<Event> eventFetchParticipation, Long userId, boolean hasNext) {

        Map<Long, Integer> regularIdParticipantMap = convertMapRegularIdParticipants(regularFetchParticipation);
        Map<Long, Set<Long>> eventIdParticipantUserIdsMap = groupEventIdParticipantIds(eventFetchParticipation);

        List<RegularEventSearchInfo> searchInfos = regularEventFetchEvent.stream()
                .map(regular -> RegularEventSearchInfo.of(
                        regular,
                        regularIdParticipantMap.get(regular.getId()),
                        eventIdParticipantUserIdsMap.get(regular.getEvent().getId()).contains(userId)))
                .toList();

        return new RegularEventSearchResponse(searchInfos, hasNext);
    }

    private static Map<Long, Integer> convertMapRegularIdParticipants(List<RegularEvent> regularEvents) {
        return regularEvents.stream()
                .collect(Collectors.toMap(
                        RegularEvent::getId,
                        regular -> regular.getParticipations().size()
                ));
    }

    private static Map<Long, Set<Long>> groupEventIdParticipantIds(List<Event> events) {
        return events.stream()
                .collect(Collectors.groupingBy(
                        Event::getId, Collectors.flatMapping(
                                event -> event.getParticipants().stream()
                                        .map(participant -> participant.getUser().getId()),
                                Collectors.toSet()
                        )
                ));
    }
}
