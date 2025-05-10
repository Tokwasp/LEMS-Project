package lems.cowshed.domain.regular.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RegularEventEditCommand {

    private String name;
    private LocalDateTime dateTime;
    private String location;
    private int capacity;

    @Builder
    private RegularEventEditCommand(String name, LocalDateTime dateTime, String location, int capacity) {
        this.name = name;
        this.dateTime = dateTime;
        this.location = location;
        this.capacity = capacity;
    }

    public static RegularEventEditCommand of(String name, LocalDateTime dateTime, String location, int capacity){
        return RegularEventEditCommand.builder()
                .name(name)
                .dateTime(dateTime)
                .location(location)
                .capacity(capacity)
                .build();
    }
}
