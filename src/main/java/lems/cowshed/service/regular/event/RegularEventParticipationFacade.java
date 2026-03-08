package lems.cowshed.service.regular.event;

import lems.cowshed.global.exception.BusinessException;
import lems.cowshed.global.exception.Message;
import lems.cowshed.global.exception.Reason;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegularEventParticipationFacade {

    private final RegularEventParticipationService regularEventParticipationService;

    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public Long saveParticipation(Long regularId, Long userId) {
        return regularEventParticipationService.saveParticipation(regularId, userId);
    }

    @Recover
    public Long recover(ObjectOptimisticLockingFailureException e, Long regularId, Long userId) {
        throw new BusinessException(Reason.REGULAR_EVENT_PARTICIPATION, Message.REGULAR_EVENT_NOT_POSSIBLE_PARTICIPATION);
    }
}