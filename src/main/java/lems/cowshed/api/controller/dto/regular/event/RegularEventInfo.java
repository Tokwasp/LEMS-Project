package lems.cowshed.api.controller.dto.regular.event;

import lems.cowshed.domain.regular.event.RegularEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RegularEventInfo {
    private long id;
    private String name;
    private String location;
    private LocalDateTime dateTime;
    private int capacity;
    private int applicants;
    private boolean isParticipated;

    @Builder
    private RegularEventInfo(long id, String name, String location, LocalDateTime dateTime,
                             int capacity, int applicants, boolean isParticipated) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.dateTime = dateTime;
        this.capacity = capacity;
        this.applicants = applicants;
        this.isParticipated = isParticipated;
    }

    public static RegularEventInfo of(RegularEvent regularEvent, int applicants, boolean isParticipated){
        return RegularEventInfo.builder()
                .id(regularEvent.getId())
                .name(regularEvent.getName())
                .location(regularEvent.getLocation())
                .dateTime(regularEvent.getDateTime())
                .capacity(regularEvent.getCapacity())
                .applicants(applicants)
                .isParticipated(isParticipated)
                .build();
    }

}