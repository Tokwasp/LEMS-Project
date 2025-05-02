package lems.cowshed.service;

import lems.cowshed.api.controller.dto.recurring.event.RecurringEventSaveRequest;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.recurring.event.RecurringEvent;
import lems.cowshed.domain.recurring.event.RecurringEventRepository;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@Transactional
@RequiredArgsConstructor
@Service
public class RecurringEventService {

    private final EventRepository eventRepository;
    private final RecurringEventRepository recurringEventRepository;

    public void save(RecurringEventSaveRequest request, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        RecurringEvent recurringEvent = request.toEntity(event);
        recurringEventRepository.save(recurringEvent);
    }
}