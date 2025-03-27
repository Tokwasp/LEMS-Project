package lems.cowshed.api.controller.dto.event.response;

import jdk.jfr.Description;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Description("모임 이름 혹은 내용 검색 결과")
public class EventsSearchInfo {
    private List<EventSimpleInfo> searchResults;

    @Builder
    public EventsSearchInfo(List<EventSimpleInfo> searchResults) {
        this.searchResults = searchResults;
    }

    public static EventsSearchInfo of(List<EventSimpleInfo> content){
        return EventsSearchInfo.builder()
                .searchResults(content)
                .build();
    }
}
