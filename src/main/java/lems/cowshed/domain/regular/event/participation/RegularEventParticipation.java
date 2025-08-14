package lems.cowshed.domain.regular.event.participation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lems.cowshed.domain.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class RegularEventParticipation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;

    private long regularEventId;

    protected RegularEventParticipation() {
    }

    @Builder
    private RegularEventParticipation(long userId, long regularEventId) {
        this.userId = userId;
        this.regularEventId = regularEventId;
    }

    public static RegularEventParticipation of(long userId, long regularEventId) {
        return RegularEventParticipation.builder()
                .userId(userId)
                .regularEventId(regularEventId)
                .build();

    }
}