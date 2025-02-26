package lems.cowshed.service;

import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPagingResponse;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.bookmark.BookmarkStatus;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.event.query.EventQueryRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.userevent.UserEvent;
import lems.cowshed.domain.userevent.UserEventRepository;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public EventPagingResponse getPagingEvents(Pageable Pageable, Long userId) {
        Slice<Event> eventPaging = eventRepository.findSliceBy(Pageable);
        List<Long> eventIdList = eventPaging.stream().map(Event::getId).toList();
        Map<Long, Long> eventCountMap = findEventParticipantsCounting(eventIdList);

        Set<Long> bookmarkedEventIds = eventRepository.findBookmarkedEventIds(userId, eventIdList, BOOKMARK);
        List<EventPreviewResponseDto> resultContent = bookmarkCountingAndCheckBookmarked(eventPaging, eventCountMap, bookmarkedEventIds);

        return EventPagingResponse.of(resultContent, eventPaging.isLast());
    }

    public void saveEvent(EventSaveRequestDto requestDto, String username) {
        Event event = requestDto.toEntity(username);
        eventRepository.save(event);
    }

    public EventDetailResponseDto getEvent(Long eventId, Long userId, String username) {
        EventDetailResponseDto response = eventQueryRepository.getEventWithParticipated(eventId);
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

    public BookmarkResponseDto getPagingBookmarkEvents(Pageable pageable, Long userId) {
        Slice<Bookmark> bookmarks = bookmarkRepository.findSliceByUserId(pageable, userId);
        List<Long> bookmarkedEventIdList = bookmarks.stream().map(bookmark -> bookmark.getEvent().getId()).toList();

        Map<Long, Long> participantsCountingMap = getCountingMap(bookmarkedEventIdList);
        List<EventPreviewResponseDto> result = bookmarks
                .stream()
                .map((Bookmark bookmark) -> EventPreviewResponseDto
                        .of(bookmark.getEvent(), participantsCountingMap.getOrDefault(bookmark.getEvent().getId(), 0L), BOOKMARK)
        ).toList();

        return BookmarkResponseDto.of(result, bookmarks.isLast());
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

    private List<EventPreviewResponseDto> bookmarkCountingAndCheckBookmarked(Slice<Event> eventPaging, Map<Long, Long> eventCountMap, Set<Long> bookmarkedEventIds) {
        List<EventPreviewResponseDto> resultContent = eventPaging
                .map(event -> EventPreviewResponseDto
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
}