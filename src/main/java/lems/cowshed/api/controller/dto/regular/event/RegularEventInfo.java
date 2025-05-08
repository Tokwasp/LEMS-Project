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
    private boolean isRegularRegistrant;

    @Builder
    private RegularEventInfo(long id, String name, String location, LocalDateTime dateTime,
                             int capacity, int applicants, boolean isParticipated, boolean isRegularRegistrant) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.dateTime = dateTime;
        this.capacity = capacity;
        this.applicants = applicants;
        this.isParticipated = isParticipated;
        this.isRegularRegistrant = isRegularRegistrant;
    }

    public static RegularEventInfo of(RegularEvent regularEvent, int applicants, boolean isParticipated,
                                        boolean isRegularRegistrant){
        return RegularEventInfo.builder()
                .id(regularEvent.getId())
                .name(regularEvent.getName())
                .location(regularEvent.getLocation())
                .dateTime(regularEvent.getDateTime())
                .capacity(regularEvent.getCapacity())
                .applicants(applicants)
                .isParticipated(isParticipated)
                .isRegularRegistrant((isRegularRegistrant))
                .build();
    }

}