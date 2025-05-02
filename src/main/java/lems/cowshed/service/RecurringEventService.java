package lems.cowshed.service;

import lems.cowshed.api.controller.dto.recurring.event.RecurringEventSaveRequest;
import lems.cowshed.domain.recurring.event.RecurringEvent;
import lems.cowshed.domain.recurring.event.RecurringEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class RecurringEventService {

    private final RecurringEventRepository recurringEventRepository;

    public void save(RecurringEventSaveRequest request) {
        RecurringEvent recurringEvent = request.toEntity();
        recurringEventRepository.save(recurringEvent);
    }
}