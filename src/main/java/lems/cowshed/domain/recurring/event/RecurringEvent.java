package lems.cowshed.domain.recurring.event;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.userevent.UserEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecurringEvent extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate date;
    private String location;
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userEvent_id")
    private UserEvent userEvent;

    @Builder
    private RecurringEvent(String name, LocalDate date, String location, int capacity, UserEvent userEvent) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.capacity = capacity;
        this.userEvent = userEvent;
    }

    public static RecurringEvent of(String name, LocalDate date, String location,
                                    int capacity, UserEvent userEvent){
        return RecurringEvent.builder()
                .name(name)
                .date(date)
                .location(location)
                .capacity(capacity)
                .userEvent(userEvent)
                .build();
    }
}