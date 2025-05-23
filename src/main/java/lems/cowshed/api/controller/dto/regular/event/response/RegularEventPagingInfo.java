package lems.cowshed.api.controller.dto.regular.event.response;

import lems.cowshed.domain.regular.event.RegularEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RegularEventPagingInfo {

    private final List<RegularEventInfo> regularEventInfos;
    private final boolean hasNext;

    @Builder
    private RegularEventPagingInfo(List<RegularEventInfo> regularEventInfos, boolean hasNext) {
        this.regularEventInfos = regularEventInfos;
        this.hasNext = hasNext;
    }

    public static RegularEventPagingInfo of(List<RegularEvent> regularEvents, Long userId, boolean hasNext){
        List<RegularEventInfo> regularEventInfos = regularEvents.stream()
                .map(regularEvent -> RegularEventInfo.of(regularEvent, userId))
                .toList();

        return RegularEventPagingInfo.builder()
                .regularEventInfos((regularEventInfos))
                .hasNext(hasNext)
                .build();
    }

    @Getter
    private static class RegularEventInfo {
        private final Long id;
        private final String name;
        private final LocalDateTime dateTime;
        private final String location;
        private final int participantsCount;
        private final int capacity;
        private final boolean isParticipated;

        @Builder
        private RegularEventInfo(Long id, String name, LocalDateTime dateTime, String location,
                                int participantsCount, int capacity, boolean isParticipated) {
            this.id = id;
            this.name = name;
            this.dateTime = dateTime;
            this.location = location;
            this.participantsCount = participantsCount;
            this.capacity = capacity;
            this.isParticipated = isParticipated;
        }

        private static RegularEventInfo of(RegularEvent regularEvent, Long userId){
            boolean isParticipated = regularEvent.getParticipations().stream()
                    .anyMatch(p -> p.getUserId().equals(userId));

            return RegularEventInfo.builder()
                    .id(regularEvent.getId())
                    .name(regularEvent.getName())
                    .dateTime(regularEvent.getDateTime())
                    .location(regularEvent.getLocation())
                    .participantsCount(regularEvent.getParticipations().size())
                    .capacity(regularEvent.getCapacity())
                    .isParticipated(isParticipated)
                    .build();
        }
    }
}
