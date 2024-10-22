package lems.cowshed.api.controller.dto.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "메인 페이지의 모임 리스트")
public class EventListResponseDto {
    @Schema(description = "모임 리스트")
    List<EventPreviewResponseDto> eventPreviewList;
}
