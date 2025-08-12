package lems.cowshed.dto.event.response;

import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.participation.EventParticipation;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class EventsSearchResponse {

    private List<EventSearchInfo> eventSearchInfos;
    private boolean hasNext;

    public EventsSearchResponse(List<EventSearchInfo> eventSearchInfos, boolean hasNext) {
        this.eventSearchInfos = eventSearchInfos;
        this.hasNext = hasNext;
    }

    public static EventsSearchResponse of(List<Event> events, Map<Long, List<EventParticipation>> groupedByEventIdMap,
                                          List<Event> eventFetchBookmarks, Long userId, boolean hasNext) {

        Map<Long, Boolean> eventIdIsBookmarkedMap = convertMapAboutEventIdIsBookmarked(eventFetchBookmarks, userId);

        List<EventSearchInfo> eventSearchInfoList = events.stream()
                .map(event -> {
                    List<EventParticipation> participants = groupedByEventIdMap.getOrDefault(event.getId(), Collections.emptyList());

                    return EventSearchInfo.of(
                            event,
                            participants.size(),
                            eventIdIsBookmarkedMap.get(event.getId()));
                }).toList();
        return new EventsSearchResponse(eventSearchInfoList, hasNext);
    }

    private static Map<Long, Boolean> convertMapAboutEventIdIsBookmarked(List<Event> events, Long userId) {
        return events.stream()
                .collect(Collectors.toMap(Event::getId,
                        event -> event.getBookmarks().stream()
                                .anyMatch(bookmark -> bookmark.getUserId().equals(userId))
                ));
    }
}
