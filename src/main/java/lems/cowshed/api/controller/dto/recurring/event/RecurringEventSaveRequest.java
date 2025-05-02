package lems.cowshed.api.controller.dto.recurring.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.recurring.event.RecurringEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RecurringEventSaveRequest {

    @NotBlank(message = "정기 모임 이름은 필수 값 입니다.")
    private String name;

    @NotNull(message = "정기 모임 일자는 필수 값 입니다.")
    private LocalDate date;

    @NotBlank(message = "정기 모임 장소는 필수 값 입니다.")
    private String location;

    @Max(value = 100, message = "정기 모임 최대 인원은 100명 입니다.")
    private int capacity;

    @Builder
    private RecurringEventSaveRequest(String name, LocalDate date, String location, int capacity) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.capacity = capacity;
    }

    public RecurringEvent toEntity(Event event) {
        return RecurringEvent.builder()
                .name(name)
                .date(date)
                .location(location)
                .capacity(capacity)
                .event(event)
                .build();
    }
}
