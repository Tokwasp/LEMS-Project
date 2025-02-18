package lems.cowshed.service;

import lems.cowshed.api.controller.dto.bookmark.response.BookmarkResponseDto;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.bookmark.BookmarkRepository;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.userevent.UserEvent;
import lems.cowshed.domain.userevent.UserEventRepository;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@Transactional
@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserEventRepository userEventRepository;

    public Slice<EventPreviewResponseDto> getPagingEvents(Pageable Pageable) {
        Slice<Event> eventPaging = eventRepository.findSliceBy(Pageable);
        Map<Long, Long> eventCountMap = findEventParticipantsCount(eventPaging);

        List<EventPreviewResponseDto> resultContent = eventPaging
                .map(event -> EventPreviewResponseDto.of(event, eventCountMap.getOrDefault(event.getId(), 0L)))
                .toList();

        return new SliceImpl<>(resultContent, Pageable, eventPaging.hasNext());
    }

    public void saveEvent(EventSaveRequestDto requestDto, String username) {
        Event event = requestDto.toEntity(username);
        eventRepository.save(event);
    }

    public EventDetailResponseDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        long participantCount = userEventRepository.countParticipantByEventId(event.getId());
        return EventDetailResponseDto.from(event, participantCount);
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
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        if(notRegisteredEventByUser(userName, event)){
            throw new BusinessException(BusinessReason, EVENT_NOT_REGISTERED_BY_USER);
        }
        event.edit(requestDto);
    }

    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));
        eventRepository.delete(event);
    }

    public BookmarkResponseDto getAllBookmarkEvents(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);
        return BookmarkResponseDto.from(bookmarks);
    }

    private Map<Long, Long> findEventParticipantsCount(Slice<Event> slice) {
        List<Long> eventIdList = slice.stream().map(Event::getId).toList();
        List<UserEvent> participantsList = userEventRepository.findByEventIdIn(eventIdList);
        return participantsList.stream()
                .collect(Collectors.groupingBy(
                        userEvent -> userEvent.getEvent().getId()
                        , Collectors.counting()
                ));
    }

    private boolean notRegisteredEventByUser(String userName, Event event) {
        return !event.getAuthor().equals(userName);
    }

    private boolean isNotPossibleToParticipate(Event event) {
        return event.isNotParticipate(userEventRepository.countParticipantByEventId(event.getId()));
    }
}