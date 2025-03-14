package lems.cowshed.api.controller.dto.event.response;

import jdk.jfr.Description;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Description("모임 서칭 검색 결과")
public class EventsSearchInfo {
    private List<EventSimpleInfo> content;

    @Builder
    public EventsSearchInfo(List<EventSimpleInfo> content) {
        this.content = content;
    }

    public static EventsSearchInfo of(List<EventSimpleInfo> content){
        return EventsSearchInfo.builder()
                .content(content)
                .build();
    }
}
