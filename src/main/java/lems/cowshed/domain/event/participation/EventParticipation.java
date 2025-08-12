package lems.cowshed.domain.event.participation;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.user.User;
import lombok.Getter;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Entity
public class EventParticipation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    private User user;

    private long eventId;

    protected EventParticipation() {
    }

    public EventParticipation(long eventId) {
        this.eventId = eventId;
    }

    public void connectUser(User user) {
        this.user = user;
    }

    public static EventParticipation of(User user, long eventId) {
        EventParticipation participation = new EventParticipation(eventId);
        participation.connectUser(user);
        return participation;
    }
}