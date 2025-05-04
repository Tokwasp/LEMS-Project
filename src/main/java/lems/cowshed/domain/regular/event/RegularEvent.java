package lems.cowshed.domain.regular.event;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.event.Event;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RegularEvent extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate date;
    private String location;
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Builder
    private RegularEvent(String name, LocalDate date, String location, int capacity, Event event) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.capacity = capacity;
        this.event = event;
    }

    public static RegularEvent of(String name, LocalDate date, String location,
                                  int capacity, Event event){
        return RegularEvent.builder()
                .name(name)
                .date(date)
                .location(location)
                .capacity(capacity)
                .event(event)
                .build();
    }

    public boolean isNotPossibleParticipation(long participantCount) {
        return capacity <= participantCount;
    }
}