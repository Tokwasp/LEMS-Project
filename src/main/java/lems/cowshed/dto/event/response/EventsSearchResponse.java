package lems.cowshed.dto.event.response;

import lems.cowshed.domain.event.Event;
import lombok.Getter;

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

    public static EventsSearchResponse of(List<Event> eventsParticipation, List<Event> eventFetchBookmarks,
                                          Long userId, boolean hasNext) {

        Map<Long, Boolean> eventMapBookmarkedByUser = mapEventIdToIsBookmarkedByUser(eventFetchBookmarks, userId);

        List<EventSearchInfo> eventSearchInfoList = eventsParticipation.stream()
                .map(event -> EventSearchInfo.of(event, eventMapBookmarkedByUser.get(event.getId())))
                .toList();

        return new EventsSearchResponse(eventSearchInfoList, hasNext);
    }

    private static Map<Long, Boolean> mapEventIdToIsBookmarkedByUser(List<Event> events, Long userId) {
        return events.stream()
                .collect(Collectors.toMap(Event::getId,
                        event -> event.getBookmarks().stream()
                                .anyMatch(bookmark -> bookmark.getUserId().equals(userId))));
    }
}
