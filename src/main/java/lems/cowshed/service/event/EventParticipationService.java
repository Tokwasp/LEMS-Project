package lems.cowshed.service.event;

import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.domain.user.User;
import lems.cowshed.domain.user.UserRepository;
import lems.cowshed.domain.userevent.UserEvent;
import lems.cowshed.domain.userevent.UserEventRepository;
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
    private final UserEventRepository userEventRepository;

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

    public void deleteEventParticipation(Long eventId, Long userId) {
        UserEvent userEvent = userEventRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(USER_EVENT, USER_EVENT_NOT_FOUND));

        userEventRepository.delete(userEvent);
    }

    private boolean isNotPossibleParticipateToEvent(Event event, long capacity) {
        return event.isOverCapacity(capacity);
    }
}
