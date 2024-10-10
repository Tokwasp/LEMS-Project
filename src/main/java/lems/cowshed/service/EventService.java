package lems.cowshed.service;

import lems.cowshed.api.controller.event.EventController;
import lems.cowshed.api.dto.event.EventDto;
import lems.cowshed.api.dto.event.EventListDto;
import lems.cowshed.api.dto.event.EventSaveDto;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.event.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<EventListDto> findAll() {
    }

    public List<EventListDto> findByCategory(String category) {
    }

    public List<EventListDto> findByKeyword(String keyword) {
    }

    public void save(EventSaveDto eventSaveDto) {
        //Event event = eventSaveDto.toEntity();
        //eventRepository.save();
    }

    public void edit(Long id) {
    }

    public void delete(Long id) {
    }

    public EventDto findById(Long id) {
    }
}
