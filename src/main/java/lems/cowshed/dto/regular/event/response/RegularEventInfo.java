package lems.cowshed.dto.regular.event.response;

import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RegularEventInfo {
    private long id;
    private Long participationId;
    private String name;
    private String location;
    private LocalDateTime dateTime;
    private int capacity;
    private int applicants;
    private boolean isRegularRegistrant;

    @Builder
    private RegularEventInfo(long id, Long participationId, String name,
                             String location, LocalDateTime dateTime, int capacity,
                             int applicants, boolean isRegularRegistrant) {
        this.id = id;
        this.participationId = participationId;
        this.name = name;
        this.location = location;
        this.dateTime = dateTime;
        this.capacity = capacity;
        this.applicants = applicants;
        this.isRegularRegistrant = isRegularRegistrant;
    }

    public static RegularEventInfo of(RegularEvent regularEvent, List<RegularEventParticipation> participants, Long userId){

        boolean isRegularRegistrant = regularEvent.getUserId().equals(userId);

        Long participationId = participants.stream()
                .filter(rep -> rep.getUserId() == userId)
                .map(RegularEventParticipation::getId)
                .findFirst()
                .orElse(null);

        return RegularEventInfo.builder()
                .id(regularEvent.getId())
                .participationId(participationId)
                .name(regularEvent.getName())
                .location(regularEvent.getLocation())
                .dateTime(regularEvent.getDateTime())
                .capacity(regularEvent.getCapacity())
                .applicants(participants.size())
                .isRegularRegistrant(isRegularRegistrant)
                .build();
    }

}