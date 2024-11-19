package lems.cowshed.domain.userevent;

import jakarta.persistence.*;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.user.User;
import lombok.Getter;
import static jakarta.persistence.FetchType.*;

@Getter
@Entity
public class UserEvent {

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

    //연관 관계 메서드
    public void relationSetter(User user, Event event){
        this.event = event;
        this.user = user;
    }

}
