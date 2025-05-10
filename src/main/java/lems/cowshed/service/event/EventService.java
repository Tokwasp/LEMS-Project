package lems.cowshed.service.event;

import lems.cowshed.api.controller.dto.event.EventIdProvider;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.*;
import lems.cowshed.api.controller.dto.regular.event.response.RegularEventInfo;
import lems.cowshed.api.controller.dto.event.response.EventParticipantsInfo;
import lems.cowshed.api.controller.dto.event.response.query.EventParticipantQueryDto;
import lems.cowshed.config.aws.AwsS3Util;
import lems.cowshed.domain.UploadFile;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventEditCommand;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.event.Events;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.event.query.BookmarkedEventSimpleInfoQuery;
import lems.cowshed.domain.event.query.EventQueryRepository;
import lems.cowshed.domain.event.query.ParticipatingEventSimpleInfoQuery;
import lems.cowshed.domain.event.participation.Participants;
import lems.cowshed.domain.event.participation.EventParticipantRepository;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.exception.Message;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@Transactional
@RequiredArgsConstructor
@Service
public class EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventQueryRepository eventQueryRepository;
    private final BookmarkRepository bookmarkRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final AwsS3Util awsS3Util;

    public EventInfo getEvent(Long eventId, String username) {
        Event event = eventQueryRepository.findEventFetchParticipants(eventId);
        int participantsCount = event.getParticipants().size();

        if(event.isNotSameAuthor(username)){
            throw new BusinessException(EVENT_AUTHOR, EVENT_NOT_REGISTERED_BY_USER);
        }

        return EventInfo.of(event, participantsCount);
    }

    public Long saveEvent(EventSaveRequestDto requestDto, String username) throws IOException {
        UploadFile uploadFile = awsS3Util.uploadFile(requestDto.getFile());
        Event event = requestDto.toEntity(username, uploadFile);
        Event savedEvent = eventRepository.save(event);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(USER_NAME, USER_NOT_FOUND));
        EventParticipation eventParticipation = EventParticipation.of(user, event);
        eventParticipantRepository.save(eventParticipation);

        return savedEvent.getId();
    }

    public void editEvent(Long eventId, EventUpdateRequestDto request, String username) throws IOException {
        Event event = eventRepository.findByIdAndAuthor(eventId, username)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        long participantCount = eventParticipantRepository.getParticipationCountById(event.getId());
        event.updateCapacity(participantCount, request.getCapacity());

        UploadFile uploadFile = awsS3Util.uploadFile(request.getFile());
        EventEditCommand eventEditCommand = createEventEditCommand(request, uploadFile);
        event.edit(eventEditCommand);
    }

    public EventParticipantsInfo getEventParticipants(Long eventId){
        List<EventParticipantQueryDto> eventParticipants = eventQueryRepository.getEventParticipants(eventId);
        return EventParticipantsInfo.of(eventParticipants, eventParticipants.size());
    }

    public EventWithRegularInfo getEventWithRegularInfo(Long eventId, Long userId, String username) {
        Event event = eventQueryRepository.findEventFetchParticipants(eventId);
        boolean isRegistrant = event.getAuthor().equals(username);
        List<Long> participantUserIds = getParticipantUserIds(event);
        boolean isParticipant = isParticipatedEvent(userId, participantUserIds);

        BookmarkStatus bookmarkStatus = bookmarkRepository.findBookmark(userId, eventId, BOOKMARK)
                .map(Bookmark::getStatus)
                .orElse(NOT_BOOKMARK);

        List<RegularEvent> regularEvents = eventQueryRepository.findRegularEventsFetchParticipants(event.getId());
        Map<Long, Integer> participantCountMap = findParticipantCountMap(regularEvents);
        List<RegularEvent> uniqueRegularEvents = findUniqueList(regularEvents);
        List<RegularEventInfo> regularEventInfos = convertRegularEventInfo(uniqueRegularEvents, participantCountMap, userId);
        return EventWithRegularInfo.of(event, isParticipant, isRegistrant, bookmarkStatus, regularEventInfos);
    }

    public EventsPagingInfo getEvents(Pageable Pageable, Long userId) {
        Slice<Event> eventsToLookFor = eventRepository.findEventsBy(Pageable);

        List<Event> content = eventsToLookFor.getContent();
        Events events = Events.of(content);
        List<Long> eventIds = events.extractIds();

        List<EventParticipation> participatedEvent = eventParticipantRepository.findEventParticipationByEventIdIn(eventIds);
        Participants participants = Participants.of(participatedEvent);
        Map<Long, Long> participantsCountByGroupId = participants.findNumberOfParticipants();

        Set<Long> userBookmarkedEventIds = eventRepository.findEventIdsBookmarkedByUser(userId, eventIds, BOOKMARK);
        List<EventSimpleInfo> result = createEventSimpleInfo(content, participantsCountByGroupId, userBookmarkedEventIds);

        return EventsPagingInfo.of(result, eventsToLookFor.isLast());
    }

    public void deleteEvent(Long eventId, String username) {
        Event event = eventRepository.findByIdAndAuthor(eventId, username).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        eventRepository.delete(event);
    }

    public BookmarkedEventsPagingInfo getEventsBookmarkedByUser(Pageable pageable, Long userId) {
        List<BookmarkedEventSimpleInfoQuery> bookmarkedEvents = eventQueryRepository.findBookmarkedEventsFromUser(userId, pageable);

        List<Long> eventIds = getEventIds(bookmarkedEvents);
        Map<Long, Long> eventParticipantCountByEventId = eventQueryRepository.findEventParticipantCountByEventIds(eventIds);
        List<BookmarkedEventSimpleInfoQuery> result = setApplicants(eventParticipantCountByEventId, bookmarkedEvents);

        return BookmarkedEventsPagingInfo.of(result);
    }

    public ParticipatingEventsPagingInfo getEventsParticipatedInUser(Pageable pageable, Long userId) {
        List<Long> eventIds = eventQueryRepository.getEventIdsParticipatedByUser(userId, pageable);
        List<ParticipatingEventSimpleInfoQuery> queryResponse = eventQueryRepository.findEventsParticipatedByUserWithApplicants(eventIds);

        List<Long> participatedEventIds = getEventIds(queryResponse);
        List<Long> bookmarkedEventIds = eventQueryRepository.getBookmarkedEventIdSet(userId, participatedEventIds);

        List<ParticipatingEventSimpleInfoQuery> bookmarkedResponse = updateBookmarkStatus(queryResponse, bookmarkedEventIds);
        return ParticipatingEventsPagingInfo.from(bookmarkedResponse);
    }

    public EventsSearchInfo searchEventsByNameOrContent(String content, Long userId) {
        List<EventSimpleInfo> EventWithbookmarkStatus = eventQueryRepository.searchEventsWithBookmarkStatus(content, userId);

        List<Long> eventIdList = getEventIds(EventWithbookmarkStatus);
        List<EventParticipation> participants = eventParticipantRepository.findEventParticipationByEventIdIn(eventIdList);
        Map<Long, Long> participantsCountByGroupId = getNumberOfParticipants(participants);

        updateApplicants(EventWithbookmarkStatus, participantsCountByGroupId);
        updateBookmarkStatus(EventWithbookmarkStatus);

        return EventsSearchInfo.of(EventWithbookmarkStatus);
    }

    private EventEditCommand createEventEditCommand(EventUpdateRequestDto request, UploadFile uploadFile) {
        return EventEditCommand.of(
                request.getName(),
                request.getCategory(),
                request.getContent(),
                request.getCapacity(),
                uploadFile
        );
    }

    private boolean isParticipatedEvent(Long userId, List<Long> participantUserIds) {
        return participantUserIds.stream()
                .anyMatch(id -> Objects.equals(userId, id));
    }

    private List<Long> getParticipantUserIds(Event event) {
        return event.getParticipants().stream()
                .map(participant -> participant.getUser().getId())
                .toList();
    }

    private <T extends EventIdProvider> List<Long> getEventIds(List<T> events) {
        return events.stream()
                .map(EventIdProvider::getEventId)
                .toList();
    }

    private void updateBookmarkStatus(List<EventSimpleInfo> searchEventWithoutApplicants) {
        searchEventWithoutApplicants.forEach(event -> {
            if (event.getBookmarkStatus() == null) {
                event.updateBookmarkStatus(NOT_BOOKMARK);
            }
        });
    }

    private List<ParticipatingEventSimpleInfoQuery> updateBookmarkStatus(List<ParticipatingEventSimpleInfoQuery> events,
                                                                         List<Long> bookmarkedEventIdSet) {
        return events.stream()
                .map(dto ->
                        ParticipatingEventSimpleInfoQuery.from(dto, bookmarkedEventIdSet.contains(dto.getId()) ? BOOKMARK : NOT_BOOKMARK))
                .toList();
    }

    private Map<Long, Long> getNumberOfParticipants(List<EventParticipation> participants) {
        return participants.stream()
                .collect(Collectors.groupingBy(
                        userEvent -> userEvent.getEvent().getId()
                        , Collectors.counting()
                ));
    }

    private List<EventSimpleInfo> createEventSimpleInfo(List<Event> events,
                                                        Map<Long, Long> eventCountMap, Set<Long> bookmarkedEventIds) {
        return events.stream()
                .map(event -> EventSimpleInfo
                        .of(event, eventCountMap.getOrDefault(event.getId(), 0L),
                                bookmarkedEventIds.contains(event.getId()) ? BOOKMARK : NOT_BOOKMARK))
                .toList();
    }

    private void updateApplicants(List<EventSimpleInfo> searchEventWithoutApplicants,
                                  Map<Long, Long> participantsCountByGroupId) {
        searchEventWithoutApplicants
                .forEach(dto -> dto.changeApplicants(participantsCountByGroupId.getOrDefault(dto.getId(), 0L)));
    }

    private List<BookmarkedEventSimpleInfoQuery> setApplicants(Map<Long, Long> eventParticipantsCountByEventId,
                                                               List<BookmarkedEventSimpleInfoQuery> bookmarkList) {
        return bookmarkList.stream()
                .map(dto ->
                        BookmarkedEventSimpleInfoQuery.updateApplicants(dto, eventParticipantsCountByEventId.getOrDefault(dto.getId(), 0L)))
                .toList();
    }

    private List<RegularEventInfo> convertRegularEventInfo(List<RegularEvent> uniqueRegularEvents,
                                                           Map<Long, Integer> participantCountMap, Long userId) {
        return uniqueRegularEvents.stream()
                .map(re ->
                        RegularEventInfo.of(re,
                                participantCountMap.get(re.getId()),
                                re.getParticipations().stream()
                                        .anyMatch(p -> p.getUserId().equals(userId)),
                                re.getUserId().equals(userId))
                )
                .toList();
    }

    private List<RegularEvent> findUniqueList(List<RegularEvent> regularEvents) {
        return regularEvents.stream()
                .distinct()
                .toList();
    }

    private Map<Long, Integer> findParticipantCountMap(List<RegularEvent> regularEvents) {
        Map<Long, Integer> participantCountMap = new HashMap<>();
        regularEvents.forEach(re -> {
            long regularId = re.getId();
            int count = re.getParticipations().size();
            participantCountMap.put(regularId, participantCountMap.getOrDefault(regularId, 0) + count);
        });
        return participantCountMap;
    }
}