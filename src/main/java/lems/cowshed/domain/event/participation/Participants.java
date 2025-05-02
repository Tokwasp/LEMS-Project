package lems.cowshed.domain.event.participation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Participants {

    private List<EventParticipant> participants;

    private Participants(List<EventParticipant> participants) {
        this.participants = participants;
    }

    public static Participants of(List<EventParticipant> participants) {
        return new Participants(participants);
    }

    public Map<Long, Long> findNumberOfParticipants() {
        return participants.stream()
                .collect(
                        Collectors.groupingBy(
                                userEvent -> userEvent.getEvent().getId()
                                , Collectors.counting()
                        ));
    }
}
