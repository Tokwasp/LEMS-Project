package lems.cowshed.dto.event.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lems.cowshed.dto.Enum;
import lems.cowshed.domain.image.UploadFile;
import lems.cowshed.domain.event.Category;
import lems.cowshed.domain.event.Event;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
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

    @Max(value = 100)
    @Schema(description = "수용 인원 ( 최대 100명 )", example = "50")
    int capacity;

    @NotBlank
    @Schema(description = "내용", example = "출근 전에 한강에서 같이 뛰실 분 구해요!!")
    String content;

    @Schema(description = "이미지 이름", example = "강아지.jpg")
    MultipartFile file;

    @Builder
    private EventSaveRequestDto(String name, Category category,
                                int capacity, String content, MultipartFile file) {
        this.name = name;
        this.category = category;
        this.capacity = capacity;
        this.content = content;
        this.file = file;
    }

    public Event toEntity(String username, UploadFile uploadFile) {
        return Event.builder()
                .name(this.name)
                .category(this.category)
                .capacity(this.getCapacity())
                .author(username)
                .content(this.content)
                .uploadFile(uploadFile)
                .build();
    }
}
