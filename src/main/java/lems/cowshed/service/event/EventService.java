package lems.cowshed.service.event;

import lems.cowshed.dto.event.EventIdProvider;
import lems.cowshed.dto.event.request.EventSaveRequestDto;
import lems.cowshed.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.dto.event.response.*;
import lems.cowshed.dto.event.response.query.EventParticipantQueryDto;
import lems.cowshed.config.aws.AwsS3Util;
import lems.cowshed.domain.UploadFile;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.repository.bookmark.BookmarkRepository;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.domain.event.Events;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.repository.event.query.BookmarkedEventSimpleInfoQuery;
import lems.cowshed.repository.event.query.EventQueryRepository;
import lems.cowshed.repository.event.query.ParticipatingEventSimpleInfoQuery;
import lems.cowshed.domain.event.participation.Participants;
import lems.cowshed.repository.event.participation.EventParticipantRepository;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.user.User;
import lems.cowshed.repository.user.UserRepository;
import lems.cowshed.exception.BusinessException;
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

@Transactional(readOnly = true)
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

        if(event.isNotSameAuthor(username)){
            throw new BusinessException(EVENT_AUTHOR, EVENT_NOT_REGISTERED_BY_USER);
        }

        return EventInfo.of(event);
    }

    @Transactional
    public Long saveEvent(EventSaveRequestDto requestDto, String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(USER_NAME, USER_NOT_FOUND));

        UploadFile uploadFile = awsS3Util.uploadFile(requestDto.getFile());
        Event event = requestDto.toEntity(username, uploadFile);

        // TODO 연관관계 메서드 분리
        EventParticipation.of(user, event);
        eventRepository.save(event);

        return event.getId();
    }

    @Transactional
    public void editEvent(Long eventId, EventUpdateRequestDto request, String username) throws IOException {
        Event event = eventRepository.findByIdAndAuthorFetchParticipation(eventId, username)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        UploadFile uploadFile = awsS3Util.uploadFile(request.getFile());

        event.modify(
                request.getName(),
                request.getCapacity(),
                request.getContent(),
                request.getCategory(),
                uploadFile,
                event.getParticipants().size()
        );
    }

    public EventParticipantsInfo getEventParticipants(Long eventId){
        List<EventParticipantQueryDto> eventParticipants = eventQueryRepository.getEventParticipants(eventId);
        return EventParticipantsInfo.of(eventParticipants, eventParticipants.size());
    }

    public EventWithRegularInfo getEventWithRegularInfo(Long eventId, Long userId, String username) {
        Event event = eventQueryRepository.findEventFetchParticipants(eventId);
        BookmarkStatus bookmarkStatus = bookmarkRepository.findBookmark(userId, eventId, BOOKMARK)
                .map(Bookmark::getStatus)
                .orElse(NOT_BOOKMARK);

        List<RegularEvent> regularEvents = eventQueryRepository.findRegularEventsFetchParticipants(event.getId());
        return EventWithRegularInfo.of(event, regularEvents, userId, username, bookmarkStatus);
    }

    //TODO 코드 리팩토링
    public EventsPagingInfo getEventsPaging(Pageable pageable, Long userId) {
        Slice<Event> eventsToLookFor = eventRepository.findEventsBy(pageable);

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

    @Transactional
    public void deleteEvent(Long eventId, String username) {
        Event event = eventRepository.findByIdAndAuthor(eventId, username).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        eventRepository.delete(event);
    }

    // TODO 코드 리팩토링
    public BookmarkedEventsPagingInfo getEventsBookmarkedByUser(Pageable pageable, Long userId) {
        List<BookmarkedEventSimpleInfoQuery> bookmarkedEvents = eventQueryRepository.findBookmarkedEventsFromUser(userId, pageable);

        List<Long> eventIds = getEventIds(bookmarkedEvents);
        Map<Long, Long> eventParticipantCountByEventId = eventQueryRepository.findEventParticipantCountByEventIds(eventIds);
        List<BookmarkedEventSimpleInfoQuery> result = setApplicants(eventParticipantCountByEventId, bookmarkedEvents);

        return BookmarkedEventsPagingInfo.of(result);
    }

    // TODO
    public ParticipatingEventsPagingInfo getEventsParticipatedInUser(Pageable pageable, Long userId) {
        List<Long> eventIds = eventQueryRepository.getEventIdsParticipatedByUser(userId, pageable);
        List<ParticipatingEventSimpleInfoQuery> queryResponse = eventQueryRepository.findEventsParticipatedByUserWithApplicants(eventIds);

        List<Long> participatedEventIds = getEventIds(queryResponse);
        List<Long> bookmarkedEventIds = eventQueryRepository.getBookmarkedEventIdSet(userId, participatedEventIds);

        List<ParticipatingEventSimpleInfoQuery> bookmarkedResponse = updateBookmarkStatus(queryResponse, bookmarkedEventIds);
        return ParticipatingEventsPagingInfo.from(bookmarkedResponse);
    }

    //TODO
    @Transactional
    public EventsSearchInfo searchEventsByNameOrContent(String content, Long userId) {
        List<EventSimpleInfo> EventWithbookmarkStatus = eventQueryRepository.searchEventsWithBookmarkStatus(content, userId);

        List<Long> eventIdList = getEventIds(EventWithbookmarkStatus);
        List<EventParticipation> participants = eventParticipantRepository.findEventParticipationByEventIdIn(eventIdList);
        Map<Long, Long> participantsCountByGroupId = getNumberOfParticipants(participants);

        updateApplicants(EventWithbookmarkStatus, participantsCountByGroupId);
        updateBookmarkStatus(EventWithbookmarkStatus);

        return EventsSearchInfo.of(EventWithbookmarkStatus);
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
}