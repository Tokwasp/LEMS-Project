package lems.cowshed.domain.event.participation;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.user.User;
import lombok.Getter;

import static jakarta.persistence.FetchType.*;

@Getter
@Entity
public class EventParticipation extends BaseEntity {

    @Id
    @Column(name = "event_participation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    protected EventParticipation() {}

    public void connectUser(User user){
        this.user = user;
    }

    public void connectEvent(Event event){
        this.event = event;
        event.getParticipants().add(this);
    }

    public static EventParticipation of(User user, Event event){
        EventParticipation participation = new EventParticipation();
        participation.connectUser(user);
        participation.connectEvent(event);
        return participation;
    }
}