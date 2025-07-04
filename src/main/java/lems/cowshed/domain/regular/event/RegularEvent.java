package lems.cowshed.domain.regular.event;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lems.cowshed.global.exception.BusinessException;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static lems.cowshed.global.exception.Message.*;
import static lems.cowshed.global.exception.Reason.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RegularEvent extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDateTime dateTime;
    private String location;
    private int capacity;
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToMany(mappedBy = "regularEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegularEventParticipation> participations = new ArrayList<>();

    @Builder
    private RegularEvent(String name, LocalDateTime dateTime, String location,
                         int capacity, Event event, Long userId) {
        this.name = name;
        this.dateTime = dateTime;
        this.location = location;
        this.capacity = capacity;
        this.event = event;
        this.userId = userId;
    }

    public static RegularEvent of(String name, LocalDateTime dateTime, String location,
                                  int capacity, Long userId, Event event){
        return RegularEvent.builder()
                .name(name)
                .dateTime(dateTime)
                .location(location)
                .capacity(capacity)
                .event(event)
                .userId(userId)
                .build();
    }

    public boolean isNotPossibleParticipation(long participantCount) {
        return capacity <= participantCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RegularEvent that = (RegularEvent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void updateCapacity(long participantCount, int updateCapacity) {
        if(participantCount > updateCapacity){
            throw new BusinessException(REGULAR_EVENT_PARTICIPATION, REGULAR_EVENT_INVALID_UPDATE_CAPACITY);
        }
        this.capacity = updateCapacity;
    }

    public void edit(RegularEventEditCommand command) {
        this.name = command.getName();
        this.dateTime = command.getDateTime();
        this.location = command.getLocation();
        this.capacity = command.getCapacity();
    }
}