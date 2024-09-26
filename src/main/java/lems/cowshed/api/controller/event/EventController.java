package lems.cowshed.api.controller.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/events")
public class EventController implements EventSpecification {

    @GetMapping("/{id}")
    public eventDto event(@PathVariable Long id) {
        return new eventDto("축구 모임", LocalDate.of(2024, 12, 25),"광화문 센터",37.5,126.9, 100,10);
    }

    @PostMapping
    public void saveEvent(@RequestBody @Validated saveEventDto event) {}

    @PatchMapping("/{id}")
    public void editEvent(@PathVariable Long id){

    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id){

    }

    @Schema(description = "모임 저장")
    @Data
    @AllArgsConstructor
    public static class saveEventDto {

        @NotBlank
        @Schema(description = "이름", example = "자전거 모임")
        String name;

        @NotBlank
        @Schema(description = "카테고리 이름", example = "취미")
        String categoryName;

        @NotBlank
        @Schema(description = "장소", example = "광화문 센터")
        String locationName;

        @Schema(description = "위도", example = "37.7")
        double latitude;

        @Schema(description = "경도", example = "126.9")
        double longitude;

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd 입니다.")
        @Schema(description = "시간", example = "2024-09-12")
        String date;

        @Max(value = 200)
        @Schema(description = "최대 참여자 수", example = "50")
        int maxParticipants;

        @NotBlank
        @Schema(description = "내용", example = "자전거 모임은 체력을 기르기 위한 모임 입니다.")
        String content;
    }


    @Schema(description = "단건 모임 조회 결과")
    @Data
    @AllArgsConstructor
    public static class eventDto {

        @Schema(description = "이름", example = "농구 모임")
        String name;

        @Schema(description = "시간", example = "2024-09-12")
        LocalDate date;

        @Schema(description = "장소", example = "광화문 센터")
        String locationName;

        @Schema(description = "위도", example = "37.7")
        double latitude;

        @Schema(description = "경도", example = "126.9")
        double longitude;

        @Schema(description = "정원", example = "100")
        int capacity;

        @Schema(description = "참여자 수", example = "50")
        int participants;
    }
}