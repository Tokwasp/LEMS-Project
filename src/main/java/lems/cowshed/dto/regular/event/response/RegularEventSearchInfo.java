package lems.cowshed.dto.regular.event.response;

import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.regular.event.RegularEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
public class RegularEventSearchInfo {
    private Long eventId;
    private String name;
    private String category;
    private String accessURL;
    private LocalDateTime dateTime;
    private boolean isEventParticipated;
    private int capacity;
    private int applicants;

    @Builder
    private RegularEventSearchInfo(Long eventId, String name, String category, String accessURL,
                                   LocalDateTime dateTime, int capacity, int applicants, boolean isEventParticipated) {
        this.eventId = eventId;
        this.name = name;
        this.category = category;
        this.accessURL = accessURL;
        this.dateTime = dateTime;
        this.isEventParticipated = isEventParticipated;
        this.capacity = capacity;
        this.applicants = applicants;
    }

    public static RegularEventSearchInfo of(RegularEvent regular, Integer applicants, boolean isEventParticipated) {
        Event event = regular.getEvent();

        return RegularEventSearchInfo.builder()
                .eventId(event.getId())
                .category(event.getCategory().getDescription())
                .accessURL(event.getUploadFile() != null ? event.getUploadFile().getAccessUrl() : null)
                .name(regular.getName())
                .dateTime(regular.getDateTime())
                .capacity(regular.getCapacity())
                .isEventParticipated(isEventParticipated)
                .applicants(applicants)
                .build();
    }
}
