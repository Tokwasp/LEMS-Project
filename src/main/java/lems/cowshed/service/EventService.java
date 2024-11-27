package lems.cowshed.service;

import lems.cowshed.api.controller.dto.event.response.EventListResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.event.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public Slice<EventPreviewResponseDto> findAll(Long lastEventId, Pageable pageable) {
        return eventRepository.findAll(lastEventId, pageable);

    }
}
