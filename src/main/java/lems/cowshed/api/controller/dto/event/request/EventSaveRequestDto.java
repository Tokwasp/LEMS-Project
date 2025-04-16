package lems.cowshed.api.controller.dto.event.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lems.cowshed.api.controller.dto.Enum;
import lems.cowshed.domain.event.Category;
import lems.cowshed.domain.event.Event;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "모임 등록")
public class EventSaveRequestDto {

    @NotBlank
    @Schema(description = "모임 이름", example = "새벽 한강 러닝 모임")
    String name;

    @Enum(message = "Category 타입을 확인해 주세요.")
    @Schema(description = "카테고리", example = "스포츠")
    Category category;

    @Max(value = 200)
    @Schema(description = "수용 인원", example = "50")
    int capacity;

    @NotBlank
    @Schema(description = "내용", example = "출근 전에 한강에서 같이 뛰실 분 구해요!!")
    String content;

    @NotBlank
    @Schema(description = "이미지 이름", example = "강아지.jpg")
    String originalFileName;

    @Builder
    private EventSaveRequestDto(String name, Category category,
                                int capacity, String content, String originalFileName) {
        this.name = name;
        this.category = category;
        this.capacity = capacity;
        this.content = content;
        this.originalFileName = originalFileName;
    }

    public Event toEntity(String username) {
        return Event.builder()
                .name(this.name)
                .category(this.category)
                .capacity(this.getCapacity())
                .author(username)
                .content(this.content)
                .originalFileName(this.originalFileName)
                .build();
    }
}
