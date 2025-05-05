package lems.cowshed.domain.event.participation;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.user.User;
import lombok.Builder;
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

    private EventParticipation() {
    }

    @Builder
    private EventParticipation(User user, Event event) {
        this.user = user;
        this.event = event;
    }

    public static EventParticipation of(User user, Event event){
        EventParticipation eventParticipation = EventParticipation.builder()
                .user(user)
                .event(event)
                .build();

        event.getParticipants().add(eventParticipation);
        return eventParticipation;
    }
}