package lems.cowshed.dto.regular.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.regular.event.RegularEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RegularEventSimpleInfo {
    @Schema(description = "정기 모임 id", example = "1")
    private Long id;

    @Schema(description = "정기 모임 이름", example = "자전거 모임")
    private String name;

    @Schema(description = "장소", example = "대구 동성로")
    private String location;

    @Schema(description = "모임 일시", example = "2025-05-10 10:50")
    private LocalDateTime dateTime;

    @Schema(description = "정원", example = "50")
    private int capacity;

    @Builder
    private RegularEventSimpleInfo(Long id, String name, String location,
                                  LocalDateTime dateTime, int capacity) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.dateTime = dateTime;
        this.capacity = capacity;
    }

    public static RegularEventSimpleInfo from(RegularEvent regularEvent){
        return RegularEventSimpleInfo.builder()
                .id(regularEvent.getId())
                .name(regularEvent.getName())
                .location(regularEvent.getLocation())
                .dateTime(regularEvent.getDateTime())
                .capacity(regularEvent.getCapacity())
                .build();
    }
}
