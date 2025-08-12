package lems.cowshed.dto.user.mypage;

import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.participation.EventParticipation;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class MyPageBookmarkedEventsInfo {

    private List<ParticipatedEventsInfo> bookmarkedEvents;
    private boolean hasNext;

    private MyPageBookmarkedEventsInfo(List<ParticipatedEventsInfo> bookmarkedEvents, boolean hasNext) {
        this.bookmarkedEvents = bookmarkedEvents;
        this.hasNext = hasNext;
    }

    public static MyPageBookmarkedEventsInfo of(List<Event> events,
                                                Map<Long, List<EventParticipation>> groupedEventIdMap, boolean hasNext) {
        List<ParticipatedEventsInfo> participatedEventsInfos = events.stream()
                .map(event -> {
                    List<EventParticipation> participants = groupedEventIdMap.getOrDefault(event.getId(), Collections.emptyList());
                    return ParticipatedEventsInfo.of(event, participants.size());
                })
                .toList();

        return new MyPageBookmarkedEventsInfo(participatedEventsInfos, hasNext);
    }

    @Getter
    static class ParticipatedEventsInfo {

        private Long eventId;
        private String name;
        private String category;
        private String content;
        private String accessUrl;
        private int applicantCount;

        @Builder
        private ParticipatedEventsInfo(Event event, int applicantCount) {
            this.eventId = event.getId();
            this.name = event.getName();
            this.category = event.getCategory().getDescription();
            this.content = event.getContent().length() > 30 ? event.getContent().substring(0, 30) : event.getContent();
            this.accessUrl = event.getUploadFile() != null ? event.getUploadFile().getAccessUrl() : null;
            this.applicantCount = applicantCount;
        }

        private static ParticipatedEventsInfo of(Event event, int applicantCount) {
            return ParticipatedEventsInfo.builder()
                    .event(event)
                    .applicantCount(applicantCount)
                    .build();
        }
    }
}
