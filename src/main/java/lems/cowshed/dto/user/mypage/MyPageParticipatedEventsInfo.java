package lems.cowshed.dto.user.mypage;

import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.participation.EventParticipation;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class MyPageParticipatedEventsInfo {

    private List<MyParticipatedEventsInfo> participatedEvents;
    private boolean hasNext;

    @Builder
    private MyPageParticipatedEventsInfo(List<MyParticipatedEventsInfo> participatedEvents, boolean hasNext) {
        this.participatedEvents = participatedEvents;
        this.hasNext = hasNext;
    }

    public static MyPageParticipatedEventsInfo fromEvents(List<Event> events,
                                                          Map<Long, List<EventParticipation>> groupedByEventIdMap, boolean hasNext) {

        List<MyParticipatedEventsInfo> result = events.stream()
                .map(event -> {
                    List<EventParticipation> participants = groupedByEventIdMap.getOrDefault(event.getId(), Collections.emptyList());
                    return MyParticipatedEventsInfo.of(event, participants.size());}
                ).toList();

        return new MyPageParticipatedEventsInfo(result, hasNext);
    }

    @Getter
    static class MyParticipatedEventsInfo {

        private Long eventId;
        private String name;
        private String content;
        private String category;
        private String accessUrl;
        private int applicantCount;

        @Builder
        private MyParticipatedEventsInfo(Event event, int applicantCount) {
            this.eventId = event.getId();
            this.name = event.getName();
            this.content = event.getContent().length() > 20 ? content.substring(0, 20) : event.getContent();
            this.category = event.getCategory().getDescription();
            this.accessUrl = event.getUploadFile() != null ? event.getUploadFile().getAccessUrl() : null;
            this.applicantCount = applicantCount;
        }

        private static MyParticipatedEventsInfo of(Event event, int applicantCount) {
            return MyParticipatedEventsInfo.builder()
                    .event(event)
                    .applicantCount(applicantCount)
                    .build();
        }
    }
}
