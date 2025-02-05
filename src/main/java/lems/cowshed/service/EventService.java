package lems.cowshed.service;

import lems.cowshed.api.controller.CommonResponse;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.userevent.UserEvent;
import lems.cowshed.domain.userevent.UserEventRepository;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@Transactional
@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final UserEventRepository userEventRepository;

    public Slice<EventPreviewResponseDto> getPagingEvents(Pageable Pageable) {
        Slice<Event> slice = eventRepository.findSliceBy(Pageable);
        return slice.map(EventPreviewResponseDto::new);
    }

    public void saveEvent(EventSaveRequestDto requestDto, String username) {
        Event event = requestDto.toEntity(username);
        eventRepository.save(event);
    }

    public EventDetailResponseDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));
        return EventDetailResponseDto.from(event);
    }

    public long joinEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        UserEvent userEvent = UserEvent.of(user, event);
        userEventRepository.save(userEvent);
        return userEvent.getId();
    }

    public void editEvent(Long eventId, EventUpdateRequestDto requestDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));
        event.edit(requestDto);
    }

    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));
        eventRepository.delete(event);
    }
}