package lems.cowshed.domain.event;

import lems.cowshed.api.controller.dto.event.request.EventSaveRequestDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository {
    Slice<EventPreviewResponseDto> findAll(Long lastEventId, Pageable pageable);
    Event findOneById(Long eventId);
    void save(Event event);
    List<Event> findAllByKeyword(String keyword);
    List<Event> findAllOrderByCreatedDate();
    List<Event> findAllOrderByApplicants();
    List<Event> findAllByCategory(String category);
//    void save(Event event);
//    Optional<Event> findById(Long id);
//    List<Event> findByCategory(Category category);
//    List<Event> findByKeyword(String keyword);
//    List<Event> findByDistance();
//    void delete(Event event);
//    void edit(Event event);
}
