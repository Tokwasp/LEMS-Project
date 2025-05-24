package lems.cowshed.service.event;

import lems.cowshed.domain.event.Event;
import lems.cowshed.repository.event.EventRepository;
import lems.cowshed.domain.event.participation.EventParticipation;
import lems.cowshed.domain.user.User;
import lems.cowshed.repository.user.UserRepository;
import lems.cowshed.repository.event.participation.EventParticipantRepository;
import lems.cowshed.exception.BusinessException;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static lems.cowshed.exception.Message.*;
import static lems.cowshed.exception.Reason.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EventParticipationService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;

    @Transactional
    public long saveEventParticipation(Long eventId, Long userId) {
        checkAlreadyParticipation(eventId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_ID, USER_NOT_FOUND));

        Event event = eventRepository.findPessimisticById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_ID, EVENT_NOT_FOUND));

        long participantCount = eventParticipantRepository.getParticipationCountById(event.getId());
        if (isNotParticipateToEvent(event, participantCount)) {
            throw new BusinessException(EVENT_CAPACITY, EVENT_CAPACITY_OVER);
        }

        EventParticipation participation = EventParticipation.of(user, event);
        eventParticipantRepository.save(participation);
        return participation.getId();
    }

    @Transactional
    public void deleteEventParticipation(Long eventId, Long userId) {
        EventParticipation eventParticipation = eventParticipantRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(EVENT_PARTICIPATION, EVENT_PARTICIPATION_FOUND));

        eventParticipantRepository.delete(eventParticipation);
    }

    private void checkAlreadyParticipation(Long eventId, Long userId) {
        eventParticipantRepository.findByEventIdAndUserId(eventId, userId)
                .ifPresent(participant -> {
                    throw new BusinessException(EVENT_PARTICIPATION, EVENT_ALREADY_PARTICIPATION);
                });
    }

    private boolean isNotParticipateToEvent(Event event, long capacity) {
        return event.isOverCapacity(capacity);
    }
}
