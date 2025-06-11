package lems.cowshed.dto.regular.event.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RegularSearchCondition {
    private String name;
    private LocalDate date;

    @Builder
    public RegularSearchCondition(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }
}
