package lems.cowshed.domain.event.participation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Participants {

    private List<EventParticipation> participants;

    private Participants(List<EventParticipation> participants) {
        this.participants = participants;
    }

    public static Participants of(List<EventParticipation> participants) {
        return new Participants(participants);
    }

    public Map<Long, Long> findNumberOfParticipants() {
        return participants.stream()
                .collect(
                        Collectors.groupingBy(
                                EventParticipation::getEventId,
                                Collectors.counting()
                        ));
    }
}
