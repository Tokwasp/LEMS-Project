package lems.cowshed.dto.regular.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.regular.event.RegularEvent;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class RegularEventSearchResponse {

    @Schema(description = "검색 결과")
    private List<RegularEventSearchInfo> searchInfos;

    @Schema(description = "다음 페이지 여부")
    private boolean hasNext;

    private RegularEventSearchResponse(List<RegularEventSearchInfo> searchInfos, boolean hasNext) {
        this.searchInfos = searchInfos;
        this.hasNext = hasNext;
    }

    public static RegularEventSearchResponse of(List<RegularEvent> regularEvents, List<RegularEvent> regularFetchParticipation, boolean hasNext) {
        Map<Long, Integer> regularIdParticipantMap = convertMapRegularIdParticipants(regularFetchParticipation);

        List<RegularEventSearchInfo> searchInfos = regularEvents.stream()
                .map(regular -> RegularEventSearchInfo.of(regular, regularIdParticipantMap.get(regular.getId())))
                .toList();

        return new RegularEventSearchResponse(searchInfos, hasNext);
    }

    private static Map<Long, Integer> convertMapRegularIdParticipants(List<RegularEvent> regularEvents) {
        return regularEvents.stream()
                .collect(Collectors.toMap(
                        RegularEvent::getId,
                        regular -> regular.getParticipations().size()
                ));
    }

}
