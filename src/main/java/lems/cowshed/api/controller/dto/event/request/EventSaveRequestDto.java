package lems.cowshed.api.controller.dto.event.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lems.cowshed.domain.event.Category;
import lems.cowshed.domain.event.Event;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "모임 등록")
public class EventSaveRequestDto {

    @NotBlank
    @Schema(description = "모임 이름", example = "새벽 한강 러닝 모임")
    String name;

    @NotNull
    @Schema(description = "카테고리", example = "스포츠")
    Category category;

    @NotBlank
    @Schema(description = "모임 장소", example = "여의도 한강공원")
    String location;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Schema(description = "모임 날짜", example = "yyyy-mm-dd HH:mm")
    LocalDateTime eventDate;

    @Max(value = 200)
    @Schema(description = "수용 인원", example = "50")
    int capacity;

    @NotBlank
    @Schema(description = "내용", example = "출근 전에 한강에서 같이 뛰실 분 구해요!!")
    String content;

    @Builder
    private EventSaveRequestDto(String name, Category category, String location, LocalDateTime eventDate, int capacity, String content) {
        this.name = name;
        this.category = category;
        this.location = location;
        this.eventDate = eventDate;
        this.capacity = capacity;
        this.content = content;
    }

    public Event toEntity() {
        return Event.builder()
                .name(this.name)
                .category(this.category)
                .location(this.location)
                .eventDate(this.eventDate)
                .capacity(this.getCapacity())
                .content(this.content)
                .build();
    }
}
