package lems.cowshed.api.controller.dto.event.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lems.cowshed.domain.event.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;
@Builder
@AllArgsConstructor
@Getter
@Schema(description = "모임 수정")
public class EventUpdateRequestDto {

    @NotBlank
    @Schema(description = "모임 이름", example = "새벽 한강 러닝 모임")
    String name;

    @NotBlank
    @Schema(description = "카테고리", example = "스포츠")
    Category category;

    @NotBlank
    @Schema(description = "모임 장소", example = "여의도 한강공원")
    String location;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd 입니다.")
    @Schema(description = "모임 날짜", example = "yyyy-mm-dd")
    LocalDateTime eventDate;

    @Max(value = 200)
    @Schema(description = "수용 인원", example = "50")
    int capacity;

    @NotBlank
    @Schema(description = "내용", example = "출근 전에 한강에서 같이 뛰실 분 구해요!!")
    String content;

}
