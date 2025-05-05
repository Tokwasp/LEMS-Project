package lems.cowshed.domain.regular.event.participation;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RegularEventParticipation extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regular_event_id")
    private RegularEvent regularEvent;

    @Builder
    private RegularEventParticipation(Long userId, RegularEvent regularEvent) {
        this.userId = userId;
        this.regularEvent = regularEvent;
    }

    public static RegularEventParticipation of(User user, RegularEvent regularEvent){
        RegularEventParticipation regularEventParticipation = RegularEventParticipation.builder()
                .userId(user.getId())
                .regularEvent(regularEvent)
                .build();
        regularEvent.getParticipations().add(regularEventParticipation);
        return regularEventParticipation;
    }
}