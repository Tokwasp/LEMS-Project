package lems.cowshed.service.event;

import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.event.participation.EventParticipant;
import lems.cowshed.domain.event.participation.EventParticipantRepository;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@Transactional
@RequiredArgsConstructor
@Service
public class EventParticipationService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;

    public long saveEventParticipation(Long eventId, Long userId) {
        Event event = eventRepository.findPessimisticLockById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        long participantsCount = eventParticipantRepository.countParticipantByEventId(event.getId());
        if (isNotPossibleParticipateToEvent(event, participantsCount)) {
            throw new BusinessException(EVENT_CAPACITY, EVENT_CAPACITY_OVER);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        EventParticipant eventParticipant = EventParticipant.of(user, event);
        eventParticipantRepository.save(eventParticipant);
        return eventParticipant.getId();
    }

    public void deleteEventParticipation(Long eventId, Long userId) {
        EventParticipant eventParticipant = eventParticipantRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(USER_EVENT, USER_EVENT_NOT_FOUND));

        eventParticipantRepository.delete(eventParticipant);
    }

    private boolean isNotPossibleParticipateToEvent(Event event, long capacity) {
        return event.isOverCapacity(capacity);
    }
}
