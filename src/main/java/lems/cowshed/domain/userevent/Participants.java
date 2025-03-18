package lems.cowshed.domain.userevent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Participants {

    private List<UserEvent> participants;

    private Participants(List<UserEvent> participants) {
        this.participants = participants;
    }

    public static Participants of(List<UserEvent> participants) {
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
