package lems.cowshed.service;

import lems.cowshed.api.controller.dto.event.EventIdProvider;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.*;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.event.Events;
import lems.cowshed.domain.event.query.BookmarkedEventSimpleInfoQuery;
import lems.cowshed.domain.event.query.EventQueryRepository;
import lems.cowshed.domain.event.query.ParticipatingEventSimpleInfoQuery;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.userevent.Participants;
import lems.cowshed.domain.userevent.UserEvent;
import lems.cowshed.domain.userevent.UserEventRepository;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@Transactional
@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventQueryRepository eventQueryRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserEventRepository userEventRepository;

    public EventsPagingInfo getEvents(Pageable Pageable, Long userId) {
        Slice<Event> eventsToLookFor = eventRepository.findEventsBy(Pageable);

        List<Event> content = eventsToLookFor.getContent();
        Events events = Events.of(content);
        List<Long> eventIds = events.extractIds();

        List<UserEvent> participatedEvent = userEventRepository.findEventParticipationByEventIdIn(eventIds);
        Participants participants = Participants.of(participatedEvent);
        Map<Long, Long> participantsCountByGroupId = participants.findNumberOfParticipants();

        Set<Long> userBookmarkedEventIds = eventRepository.findEventIdsBookmarkedByUser(userId, eventIds, BOOKMARK);
        List<EventSimpleInfo> result = createEventSimpleInfo(content, participantsCountByGroupId, userBookmarkedEventIds);

        return EventsPagingInfo.of(result, eventsToLookFor.isLast());
    }

    public void saveEvent(EventSaveRequestDto requestDto, String username) {
        Event event = requestDto.toEntity(username);
        eventRepository.save(event);
    }

    public long saveEventParticipation(Long eventId, Long userId) {
        Event event = eventRepository.findPessimisticLockById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        long participantsCount = userEventRepository.countParticipantByEventId(event.getId());
        if (isNotPossibleParticipateToEvent(event, participantsCount)) {
            throw new BusinessException(EVENT_CAPACITY, EVENT_CAPACITY_OVER);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        UserEvent userEvent = UserEvent.of(user, event);
        userEventRepository.save(userEvent);
        return userEvent.getId();
    }

    public EventInfo getEvent(Long eventId, Long userId, String username) {
        EventInfo response = eventQueryRepository.findEventWithApplicantUserIds(eventId);

        Optional<Bookmark> bookmark = bookmarkRepository.findBookmark(userId, eventId, BOOKMARK);
        BookmarkStatus bookmarkStatus = bookmark.isPresent() ? BOOKMARK : NOT_BOOKMARK;
        response.updateBookmarkStatus(bookmarkStatus);

        boolean isEventRegistrant = response.isEventRegistrant(username);
        response.updateRegistrant(isEventRegistrant);

        boolean isParticipated = findIsParticipated(userId, response.getUserList());
        response.updateParticipated(isParticipated);

        return response;
    }

    public void editEvent(Long eventId, EventUpdateRequestDto requestDto, String userName) {
        Event event = eventRepository.findByIdAndAuthor(eventId, userName)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        if (isNotRegisteredEventByUser(event, userName)) {
            throw new BusinessException(BusinessReason, EVENT_NOT_REGISTERED_BY_USER);
        }

        event.edit(requestDto);
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

    public void deleteEventParticipation(Long eventId, Long userId) {
        UserEvent userEvent = userEventRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(USER_EVENT, USER_EVENT_NOT_FOUND));

        userEventRepository.delete(userEvent);
    }

    public EventsSearchInfo searchEventsByNameOrContent(String content, Long userId) {
        List<EventSimpleInfo> EventWithbookmarkStatus = eventQueryRepository.searchEventsWithBookmarkStatus(content, userId);

        List<Long> eventIdList = getEventIds(EventWithbookmarkStatus);
        List<UserEvent> participants = userEventRepository.findEventParticipationByEventIdIn(eventIdList);
        Map<Long, Long> participantsCountByGroupId = getNumberOfParticipants(participants);

        updateApplicants(EventWithbookmarkStatus, participantsCountByGroupId);
        updateBookmarkStatus(EventWithbookmarkStatus);

        return EventsSearchInfo.of(EventWithbookmarkStatus);
    }

    private boolean isNotRegisteredEventByUser(Event event, String userName) {
        return event.isNotSameAuthor(userName);
    }

    private boolean isNotPossibleParticipateToEvent(Event event, long capacity) {
        return event.isOverCapacity(capacity);
    }

    private Boolean findIsParticipated(Long userId, String userIds) {
        return Optional.ofNullable(userIds)
                .map(s -> Arrays.stream(userIds.split(","))
                        .map(Long::parseLong)
                        .anyMatch(id -> id.equals(userId)))
                .orElse(false);
    }

    private <T extends EventIdProvider> List<Long> getEventIds(List<T> events){
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

    private Map<Long, Long> getNumberOfParticipants(List<UserEvent> participants) {
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