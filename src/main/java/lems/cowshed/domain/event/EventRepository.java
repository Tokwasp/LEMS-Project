package lems.cowshed.domain.event;

import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface EventRepository {
    Slice<EventPreviewResponseDto> findAll(Long lastEventId, Pageable pageable);
//    void save(Event event);
//    Optional<Event> findById(Long id);
//    List<Event> findByCategory(Category category);
//    List<Event> findByKeyword(String keyword);
//    List<Event> findByDistance();
//    void delete(Event event);
//    void edit(Event event);

}
