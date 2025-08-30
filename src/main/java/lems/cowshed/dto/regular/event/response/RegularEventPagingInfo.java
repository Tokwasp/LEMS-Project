package lems.cowshed.dto.regular.event.response;

import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class RegularEventPagingInfo {

    private final List<RegularEventInfo> regularEventInfos;
    private final boolean hasNext;

    @Builder
    private RegularEventPagingInfo(List<RegularEventInfo> regularEventInfos, boolean hasNext) {
        this.regularEventInfos = regularEventInfos;
        this.hasNext = hasNext;
    }

    public static RegularEventPagingInfo of(List<RegularEvent> regularEvents, Map<Long, List<RegularEventParticipation>> groupedIdMap,
                                            Long userId, boolean hasNext) {

        List<RegularEventInfo> regularEventInfos = regularEvents.stream()
                .map(regularEvent -> {
                    List<RegularEventParticipation> participants = groupedIdMap.getOrDefault(regularEvent.getId(), Collections.emptyList());
                    return RegularEventInfo.of(regularEvent, participants, userId);
                })
                .toList();

        return RegularEventPagingInfo.builder()
                .regularEventInfos(regularEventInfos)
                .hasNext(hasNext)
                .build();
    }

    @Getter
    public static class RegularEventInfo {
        private Long id;
        private Long participationId;
        private String name;
        private LocalDateTime dateTime;
        private String location;
        private int participantsCount;
        private int capacity;
        private boolean isRegistrant;

        @Builder
        private RegularEventInfo(Long id, Long participationId, String name, LocalDateTime dateTime,
                                 String location, int participantsCount, int capacity, boolean isRegistrant) {
            this.id = id;
            this.participationId = participationId;
            this.name = name;
            this.dateTime = dateTime;
            this.location = location;
            this.participantsCount = participantsCount;
            this.capacity = capacity;
            this.isRegistrant = isRegistrant;
        }

        private static RegularEventInfo of(RegularEvent regularEvent, List<RegularEventParticipation> participants, Long userId) {
            Long participationId = getParticipationId(participants, userId);
            boolean isRegistrant = regularEvent.getUserId().equals(userId);

            return RegularEventInfo.builder()
                    .id(regularEvent.getId())
                    .participationId(participationId)
                    .name(regularEvent.getName())
                    .dateTime(regularEvent.getDateTime())
                    .location(regularEvent.getLocation())
                    .participantsCount(participants.size())
                    .capacity(regularEvent.getCapacity())
                    .isRegistrant(isRegistrant)
                    .build();
        }

        private static Long getParticipationId(List<RegularEventParticipation> participants, Long userId) {
            return participants.stream()
                    .filter(p -> p.getUserId() == (userId))
                    .map(RegularEventParticipation::getId)
                    .findFirst()
                    .orElse(null);
        }
    }
}
