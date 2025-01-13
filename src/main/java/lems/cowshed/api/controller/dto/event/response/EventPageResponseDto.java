package lems.cowshed.api.controller.dto.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "모임 페이징 결과")
public class EventPageResponseDto {

    List<EventPreviewResponseDto> eventList;
}
