package lems.cowshed.dto.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "메인 페이지의 모임 리스트")
public class EventsPagingResponse {
    private List<EventPagingInfo> content;
    private boolean isLast;

    @Builder
    public EventsPagingResponse(List<EventPagingInfo> content, boolean isLast) {
        this.content = content;
        this.isLast = isLast;
    }

    public static EventsPagingResponse of(List<EventPagingInfo> content, boolean isLast){
        return EventsPagingResponse.builder()
                .content(content)
                .isLast(isLast)
                .build();
    }
}
