package lems.cowshed.service.event;

import lems.cowshed.global.exception.BusinessException;
import lems.cowshed.global.exception.Message;
import lems.cowshed.global.exception.Reason;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventParticipationFacade {

    private final EventParticipationService eventParticipationService;

    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void saveEventParticipation(Long eventId, Long userId) {
        eventParticipationService.saveEventParticipation(eventId, userId);
    }

    @Recover
    public void recover(ObjectOptimisticLockingFailureException e, Long eventId, Long userId) {
        throw new BusinessException(Reason.EVENT_PARTICIPATION, Message.EVENT_PARTICIPATION_CONCURRENT);
    }
}
