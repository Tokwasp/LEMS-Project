package lems.cowshed.domain.regular.event.participation;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.regular.event.RegularEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class RegularEventParticipation extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regular_event_id")
    private RegularEvent regularEvent;

    protected RegularEventParticipation() {}

    @Builder
    private RegularEventParticipation(Long userId) {
        this.userId = userId;
    }

    public static RegularEventParticipation of(Long userId, RegularEvent regularEvent){
        RegularEventParticipation regularEventParticipation = RegularEventParticipation.builder()
                .userId(userId)
                .build();

        regularEventParticipation.connectRegularEvent(regularEvent);
        return regularEventParticipation;
    }

    public void connectRegularEvent(RegularEvent regularEvent){
        this.regularEvent = regularEvent;
        regularEvent.getParticipations().add(this);
    }
}