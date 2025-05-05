package lems.cowshed.service.event;

import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
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
        checkAlreadyParticipation(eventId, userId);

        Event event = findEvent(eventId);
        eventParticipation(event);

        User user = findUser(userId);
        EventParticipation eventParticipation = EventParticipation.of(user, event);
        return eventParticipantRepository.save(eventParticipation).getId();
    }

    public void deleteEventParticipation(Long eventId, Long userId) {
        EventParticipation eventParticipation = eventParticipantRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(EVENT_PARTICIPATION, EVENT_PARTICIPATION_FOUND));

        eventParticipantRepository.delete(eventParticipation);
    }

    private boolean isNotPossibleParticipateToEvent(Event event, long capacity) {
        return event.isOverCapacity(capacity);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findEventWithLockById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));
    }

    private void eventParticipation(Event event) {
        long participantCount = eventParticipantRepository.getParticipationCountById(event.getId());
        if (isNotPossibleParticipateToEvent(event, participantCount)) {
            throw new BusinessException(EVENT_CAPACITY, EVENT_CAPACITY_OVER);
        }
    }

    private void checkAlreadyParticipation(Long eventId, Long userId) {
        eventParticipantRepository.findByEventIdAndUserId(eventId, userId)
                .ifPresent(participant -> {throw new BusinessException(EVENT_PARTICIPATION, EVENT_ALREADY_PARTICIPATION);});
    }
}
