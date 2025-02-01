package lems.cowshed.domain.userevent;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import static jakarta.persistence.FetchType.*;

@Getter
@Entity
public class UserEvent extends BaseEntity {

    @Id
    @Column(name = "user_event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Builder
    private UserEvent(User user, Event event) {
        this.user = user;
        this.event = event;
    }

    public static UserEvent of(User user, Event event){
        UserEvent userEvent = UserEvent.builder()
                .user(user)
                .event(event)
                .build();
        return userEvent;
    }
}