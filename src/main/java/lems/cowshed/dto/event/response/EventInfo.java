package lems.cowshed.dto.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.event.Category;
import lems.cowshed.domain.event.Event;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "모임 상세")
public class EventInfo {
    @Schema(description = "모임 id", example = "1")
    Long eventId;
    @Schema(description = "모임 이름", example = "농구 모임")
    String name;
    @Schema(description = "카테고리", example = "스포츠")
    Category category;
    @Schema(description = "내용", example = "같이 운동하실 분 구합니다. 같이 프레스 운동 하면서 서로 보조해주실 분 구합니다.")
    String content;
    @Schema(description = "수용 인원", example = "100")
    int capacity;
    @Schema(description = "참여 신청 인원", example = "50")
    long applicants;
    @Schema(description = "모임 대표 이미지 주소", example = "URL 주소")
    private String accessUrl;

    @Builder
    private EventInfo(Long eventId, String name, Category category,
                      String content, int capacity, long applicants, String accessUrl) {
        this.eventId = eventId;
        this.name = name;
        this.category = category;
        this.content = content;
        this.capacity = capacity;
        this.applicants = applicants;
        this.accessUrl = accessUrl;
    }

    public static EventInfo of(Event event, int participantsCount) {
        return EventInfo.builder()
                .eventId(event.getId())
                .name(event.getName())
                .category(event.getCategory())
                .accessUrl(event.getUploadFile() != null ? event.getUploadFile().getAccessUrl() : null)
                .content(event.getContent())
                .capacity(event.getCapacity())
                .applicants(participantsCount)
                .build();
    }
}