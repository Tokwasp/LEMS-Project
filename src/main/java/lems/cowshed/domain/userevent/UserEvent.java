package lems.cowshed.domain.userevent;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class UserEvent {
    @Id
    @Column(name = "user_event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private int userId;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private int eventId;

}
