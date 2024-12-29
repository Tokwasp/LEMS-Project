package lems.cowshed.service;

import jakarta.persistence.NoResultException;
import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.request.EventUpdateRequestDto;
import lems.cowshed.api.controller.dto.event.response.EventListResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import lems.cowshed.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lems.cowshed.exception.Message.EVENT_NOT_FOUND;
import static lems.cowshed.exception.Reason.EVENT_ID;

@RequiredArgsConstructor
@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public Slice<EventPreviewResponseDto> findAll(Long lastEventId, Pageable pageable) {
        return eventRepository.findAll(lastEventId, pageable);

    }

    public void create(EventSaveRequestDto requestDto){
        eventRepository.save(requestDto.toEntity());
    }
    public void update(Long eventId, EventUpdateRequestDto requestDto){
        Event event;
        try{
            event = eventRepository.findOneById(eventId);
        }catch(NoResultException e){
            throw new NotFoundException(EVENT_ID, EVENT_NOT_FOUND);
        }
        event.update(requestDto.getName(), requestDto.getCategory(), requestDto.getLocation(), requestDto.getEventDate(), requestDto.getCapacity(), requestDto.getContent());
    }
    public void delete(Long eventId){
        try{
            Event event = eventRepository.findOneById(eventId);
        }catch(NoResultException e){
            throw new NotFoundException(EVENT_ID, EVENT_NOT_FOUND);
        }

    }

}
