package lems.cowshed.api.controller.dto.event.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lems.cowshed.api.controller.dto.Enum;
import lems.cowshed.domain.event.Category;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Schema(description = "모임 수정")
public class EventUpdateRequestDto {

    @NotBlank
    @Schema(description = "모임 이름", example = "새벽 한강 러닝 모임")
    String name;

    @Enum(message = "Category 타입을 확인해 주세요.")
    @Schema(description = "카테고리", example = "스포츠")
    Category category;

    @NotBlank
    @Schema(description = "내용", example = "출근 전에 한강에서 같이 뛰실 분 구해요!!")
    String content;

    @Max(value = 100)
    @Schema(description = "수용 인원 ( 최대 100명 )", example = "50")
    int capacity;

    @Schema(description = "이미지 이름", example = "강아지.jpg")
    MultipartFile file;

    @Builder
    private EventUpdateRequestDto(String name, Category category, String content,
                                  int capacity, MultipartFile file) {
        this.name = name;
        this.category = category;
        this.content = content;
        this.capacity = capacity;
        this.file = file;
    }

    private EventUpdateRequestDto() {}

}
