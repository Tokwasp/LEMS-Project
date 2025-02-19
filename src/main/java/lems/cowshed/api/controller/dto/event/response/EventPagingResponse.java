package lems.cowshed.api.controller.dto.event.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class EventPagingResponse {
    private List<EventPreviewResponseDto> content;
    private boolean isLast;

    @Builder
    public EventPagingResponse(List<EventPreviewResponseDto> content, boolean isLast) {
        this.content = content;
        this.isLast = isLast;
    }

    public static EventPagingResponse of(List<EventPreviewResponseDto> content, boolean isLast){
        return EventPagingResponse.builder()
                .content(content)
                .isLast(isLast)
                .build();
    }
}
