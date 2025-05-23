package lems.cowshed.dto.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "메인 페이지의 모임 리스트")
public class EventsPagingInfo {
    private List<EventSimpleInfo> content;
    private boolean isLast;

    @Builder
    public EventsPagingInfo(List<EventSimpleInfo> content, boolean isLast) {
        this.content = content;
        this.isLast = isLast;
    }

    public static EventsPagingInfo of(List<EventSimpleInfo> content, boolean isLast){
        return EventsPagingInfo.builder()
                .content(content)
                .isLast(isLast)
                .build();
    }
}
