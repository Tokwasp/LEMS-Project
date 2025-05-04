package lems.cowshed.domain.regular.event;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.regular.event.participation.RegularEventParticipation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegularEventParticipation> participations = new ArrayList<>();

    @Builder
    private RegularEvent(String name, LocalDateTime dateTime, String location, int capacity, Event event) {
        this.name = name;
        this.dateTime = dateTime;
        this.location = location;
        this.capacity = capacity;
        this.event = event;
    }

    public static RegularEvent of(String name, LocalDateTime dateTime, String location,
                                  int capacity, Event event){
        return RegularEvent.builder()
                .name(name)
                .dateTime(dateTime)
                .location(location)
                .capacity(capacity)
                .event(event)
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
}