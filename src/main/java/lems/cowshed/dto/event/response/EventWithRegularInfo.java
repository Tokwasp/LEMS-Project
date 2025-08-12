package lems.cowshed.dto.event.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Category;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.dto.regular.event.response.RegularEventInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "모임/ 정기모임 상세")
public class EventWithRegularInfo {

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

    @Schema(description = "북마크 여부", example = "BOOKMARK")
    BookmarkStatus bookmarkStatus;

    @Schema(description = "모임 대표 이미지 주소", example = "URL 주소")
    private String accessUrl;

    @Schema(description = "내가 등록한 모임 인지 여부", example = "true")
    boolean isEventRegistrant;

    @Schema(description = "내가 참여한 모임 인지 여부", example = "true")
    boolean isParticipated;

    @Schema(description = "정기 모임 내용")
    private List<RegularEventInfo> regularEvents;

    @Builder
    private EventWithRegularInfo(Long eventId, String name, Category category,
                                 String content, int capacity, long applicants,
                                 String accessUrl, List<RegularEventInfo> regularEvents, BookmarkStatus bookmarkStatus,
                                 boolean isEventRegistrant, boolean isParticipated) {
        this.eventId = eventId;
        this.name = name;
        this.category = category;
        this.content = content;
        this.capacity = capacity;
        this.applicants = applicants;
        this.bookmarkStatus = bookmarkStatus;
        this.accessUrl = accessUrl;
        this.isEventRegistrant = isEventRegistrant;
        this.isParticipated = isParticipated;
        this.regularEvents = regularEvents;
    }

    public static EventWithRegularInfo of(Event event, List<EventParticipation> participants,
                                          List<RegularEvent> regularEvents, Long userId,
                                          String username, BookmarkStatus bookmarkStatus) {

        boolean isParticipated = isEventParticipatedUser(participants, userId);
        List<RegularEventInfo> regularEventsInfo = convertToResponses(regularEvents, userId);
        String accessUrl = getAccessUrl(event);

        return EventWithRegularInfo.builder()
                .eventId(event.getId())
                .name(event.getName())
                .category(event.getCategory())
                .accessUrl(accessUrl)
                .content(event.getContent())
                .capacity(event.getCapacity())
                .applicants(participants.size())
                .bookmarkStatus(bookmarkStatus)
                .isEventRegistrant(event.getAuthor().equals(username))
                .isParticipated(isParticipated)
                .regularEvents(regularEventsInfo)
                .build();
    }

    private static String getAccessUrl(Event event) {
        return event.getUploadFile() != null ? event.getUploadFile().getAccessUrl() : null;
    }

    private static List<RegularEventInfo> convertToResponses(List<RegularEvent> regularEvents, Long userId) {
        return regularEvents.stream()
                .map(re -> RegularEventInfo.of(re, userId))
                .toList();
    }

    private static boolean isEventParticipatedUser(List<EventParticipation> participants, Long userId) {
        List<Long> participantsUserIds = participants.stream()
                .map(participant -> participant.getUser().getId())
                .toList();

        return participantsUserIds.stream()
                .anyMatch(id -> id.equals(userId));
    }
}