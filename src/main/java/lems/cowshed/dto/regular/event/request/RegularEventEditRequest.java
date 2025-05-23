package lems.cowshed.dto.regular.event.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.regular.event.RegularEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RegularEventEditRequest {

    @NotBlank(message = "정기 모임 이름은 필수 값 입니다.")
    private String name;

    @NotNull(message = "정기 모임 일자는 필수 값 입니다.")
    private LocalDateTime dateTime;

    @NotBlank(message = "정기 모임 장소는 필수 값 입니다.")
    private String location;

    @Max(value = 100, message = "정기 모임 최대 인원은 100명 입니다.")
    private int capacity;

    @Builder
    private RegularEventEditRequest(String name, LocalDateTime dateTime, String location, int capacity) {
        this.name = name;
        this.dateTime = dateTime;
        this.location = location;
        this.capacity = capacity;
    }

    public RegularEvent toEntity(Event event, Long userId) {
        return RegularEvent.builder()
                .name(name)
                .dateTime(dateTime)
                .location(location)
                .capacity(capacity)
                .event(event)
                .userId(userId)
                .build();
    }
}
