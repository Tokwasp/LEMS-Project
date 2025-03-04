package lems.cowshed.service;

import com.querydsl.core.Tuple;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.*;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.event.query.BookmarkedEventSimpleInfoQuery;
import lems.cowshed.domain.event.query.EventQueryRepository;
import lems.cowshed.domain.event.query.ParticipatingEventSimpleInfoQuery;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.userevent.UserEvent;
import lems.cowshed.domain.userevent.UserEventRepository;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static lems.cowshed.domain.userevent.QUserEvent.userEvent;
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

    public EventsPagingInfo getPagingEvents(Pageable Pageable, Long userId) {
        Slice<Event> eventPaging = eventRepository.findSliceBy(Pageable);
        List<Long> eventIdList = eventPaging.stream().map(Event::getId).toList();
        Map<Long, Long> eventCountMap = findEventParticipantsCounting(eventIdList);

        Set<Long> bookmarkedEventIds = eventRepository.findBookmarkedEventIds(userId, eventIdList, BOOKMARK);
        List<EventSimpleInfo> resultContent = bookmarkCountingAndCheckBookmarked(eventPaging, eventCountMap, bookmarkedEventIds);

        return EventsPagingInfo.of(resultContent, eventPaging.isLast());
    }

    public void saveEvent(EventSaveRequestDto requestDto, String username) {
        Event event = requestDto.toEntity(username);
        eventRepository.save(event);
    }

    public EventInfo getEvent(Long eventId, Long userId, String username) {
        EventInfo response = eventQueryRepository.getEventWithParticipated(eventId);
        BookmarkStatus bookmarkStatus = bookmarkRepository.findBookmark(userId, eventId, BOOKMARK)
                .isEmpty() ? NOT_BOOKMARK : BOOKMARK;

        response.setParticipatedAndRegisteredByMeAndBookmarked(userId, username, bookmarkStatus);
        return response;
    }

    public long joinEvent(Long eventId, Long userId) {
        Event event = eventRepository.findPessimisticLockById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        if(isNotPossibleToParticipate(event)){
            throw new BusinessException(EVENT_CAPACITY, EVENT_CAPACITY_OVER);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        UserEvent userEvent = UserEvent.of(user, event);
        userEventRepository.save(userEvent);
        return userEvent.getId();
    }

    public void editEvent(Long eventId, EventUpdateRequestDto requestDto, String userName) {
        Event event = eventRepository.findByIdAndAuthor(eventId, userName).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        if(notRegisteredEventByUser(userName, event)){
            throw new BusinessException(BusinessReason, EVENT_NOT_REGISTERED_BY_USER);
        }
        event.edit(requestDto);
    }

    public void deleteEvent(Long eventId, String username) {
        Event event = eventRepository.findByIdAndAuthor(eventId, username).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        eventRepository.delete(event);
    }

    public BookmarkedEventsPagingInfo getBookmarkedEventsPaging(Pageable pageable, Long userId) {
        List<BookmarkedEventSimpleInfoQuery> bookmarkedEventList = eventQueryRepository
                .findBookmarkedEventsPaging(userId, pageable);

        setApplicants(eventQueryRepository.findEventIdParticipants(mapToEventIdList(bookmarkedEventList)), bookmarkedEventList);
        return BookmarkedEventsPagingInfo.of(bookmarkedEventList);
    }

    public ParticipatingEventsPagingInfo getParticipatingEventsPaging(Pageable pageable, Long userId) {
        List<ParticipatingEventSimpleInfoQuery> participatedEvents = eventQueryRepository
                .findParticipatedEvents(eventQueryRepository.getParticipatedEventsId(userId, pageable));

        setBookmarkStatus(participatedEvents, eventQueryRepository.getBookmarkedEventIdSet(userId, getParticipatedEventIds(participatedEvents)));
        return ParticipatingEventsPagingInfo.from(participatedEvents);
    }

    public void deleteUserEvent(Long eventId, Long userId) {
        UserEvent userEvent = userEventRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(USER_EVENT, USER_EVENT_NOT_FOUND));

        userEventRepository.delete(userEvent);
    }

    private Map<Long, Long> findEventParticipantsCounting(List<Long> eventIdList) {
        List<UserEvent> participantsList = userEventRepository.findByEventIdIn(eventIdList);
        return participantsList.stream()
                .collect(Collectors.groupingBy(
                        userEvent -> userEvent.getEvent().getId()
                        , Collectors.counting()
                ));
    }

    private Map<Long, Long> getCountingMap(List<Long> bookmarkedEventIdList) {
        List<UserEvent> participantsList = userEventRepository.findByEventIdIn(bookmarkedEventIdList);
        return participantsList.stream()
                .collect(Collectors.groupingBy(
                        userEvent -> userEvent.getEvent().getId()
                        , Collectors.counting()
                ));
    }

    private List<EventSimpleInfo> bookmarkCountingAndCheckBookmarked(Slice<Event> eventPaging, Map<Long, Long> eventCountMap, Set<Long> bookmarkedEventIds) {
        List<EventSimpleInfo> resultContent = eventPaging
                .map(event -> EventSimpleInfo
                        .of(event, eventCountMap.getOrDefault(event.getId(), 0L),
                                bookmarkedEventIds.contains(event.getId()) ? BOOKMARK : NOT_BOOKMARK))
                .toList();
        return resultContent;
    }

    private boolean notRegisteredEventByUser(String userName, Event event) {
        return !event.getAuthor().equals(userName);
    }

    private boolean isNotPossibleToParticipate(Event event) {
        return event.isNotParticipate(userEventRepository.countParticipantByEventId(event.getId()));
    }

    private List<Long> mapToEventIdList(List<BookmarkedEventSimpleInfoQuery> bookmarkedEventList) {
        return bookmarkedEventList.stream().map(BookmarkedEventSimpleInfoQuery::getId).toList();
    }

    private void setApplicants(List<Tuple> eventIdParticipants, List<BookmarkedEventSimpleInfoQuery> bookmarkList) {
        Map<Long, Long> eventIdParticipantsMap = eventIdParticipants.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(userEvent.event.id),
                        tuple -> Optional.ofNullable(tuple.get(userEvent.event.id.count())).orElse(0L)
                ));

        bookmarkList
                .forEach(dto -> dto.setApplicants(eventIdParticipantsMap.getOrDefault(dto.getId(), 0L)));
    }

    private List<Long> getParticipatedEventIds(List<ParticipatingEventSimpleInfoQuery> participatedEvents) {
        return participatedEvents.stream()
                .map(ParticipatingEventSimpleInfoQuery::getId).toList();
    }

    private void setBookmarkStatus(List<ParticipatingEventSimpleInfoQuery> participatedEvents, List<Long> bookmarkedEventIdSet) {
        participatedEvents
                .forEach(dto -> dto.setBookmarkStatus(bookmarkedEventIdSet.contains(dto.getId()) ? BOOKMARK : NOT_BOOKMARK));
    }
}