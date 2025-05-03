package lems.cowshed.service;

import lems.cowshed.api.controller.dto.recurring.event.RegularEventSaveRequest;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.domain.regular.event.RegularEventRepository;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@Transactional
@RequiredArgsConstructor
@Service
public class RegularEventService {

    private final EventRepository eventRepository;
    private final RegularEventRepository regularEventRepository;

    public void save(RegularEventSaveRequest request, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        RegularEvent regularEvent = request.toEntity(event);
        regularEventRepository.save(regularEvent);
    }

    public void saveParticipation(Long regularId, Long userId) {

    }
}