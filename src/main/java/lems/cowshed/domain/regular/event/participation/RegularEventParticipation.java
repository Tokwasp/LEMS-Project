package lems.cowshed.domain.regular.event.participation;

import jakarta.persistence.*;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.user.User;
import lombok.Builder;

@Entity
public class RegularEventParticipation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurring_event_id")
    private RegularEvent regularEvent;

    @Builder
    private RegularEventParticipation(Long userId, RegularEvent regularEvent) {
        this.userId = userId;
        this.regularEvent = regularEvent;
    }

    public static RegularEventParticipation of(User user, RegularEvent regularEvent){
        return RegularEventParticipation.builder()
                .userId(user.getId())
                .regularEvent(regularEvent)
                .build();
    }
}