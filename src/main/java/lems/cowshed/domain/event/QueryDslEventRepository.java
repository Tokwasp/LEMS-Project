package lems.cowshed.domain.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.api.controller.dto.event.response.QEventPreviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static lems.cowshed.domain.event.QEvent.*;
@RequiredArgsConstructor
@Repository
@Slf4j
public class QueryDslEventRepository implements EventRepository{
    private final JPAQueryFactory qf;
    private final EntityManager em;

    public void save(Event event){
        em.persist(event);
    }

    public Slice<EventPreviewResponseDto> findAll(Long lastEventId, Pageable pageable){
        List<EventPreviewResponseDto> results = qf
                .select(new QEventPreviewResponseDto(
                        event.id,
                        event.name,
                        event.content,
                        event.eventDate,
                        event.capacity,
                        event.applicants,
                        event.createdDateTime
                        ))
                .from(event)
                .where(ifExistNextPage(lastEventId)) //no-offset paging
                .orderBy(event.id.desc())
                .limit(pageable.getPageSize()+1)
                .fetch();

        return slicePage(pageable, results);
    }

    private BooleanExpression ifExistNextPage(Long lastEventId) {
        return lastEventId == null ? null : event.id.lt(lastEventId);
    }

    private Slice<EventPreviewResponseDto> slicePage(Pageable pageable, List<EventPreviewResponseDto> results){
        //조회 결과가 요청한 페이지 사이즈보다 크면 hasNext == true
        boolean hasNext = results.size() > pageable.getPageSize();
        if(hasNext){
            results.remove(pageable.getPageSize());
        }
        return new SliceImpl(results, pageable, hasNext);
    }

    //TODO; paging, refactoring to QueryDSL
    public List<Event> findAllByCategory(String category){
        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e" + "WHERE category=:category", Event.class);
        query.setParameter("category", category);
        List<Event> events = query.getResultList();
        return events;
    }

    //TODO; paging, refactoring to QueryDSL
    public List<Event> findAllOrderByApplicants(){
        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e" + "ORDER BY e.applicants ASC", Event.class);
        List<Event> events = query.getResultList();
        return events;
    }

    //TODO; paging, refactoring to QueryDSL
    public List<Event> findAllOrderByCreatedDate(){
        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e" + "ORDER BY e.createdDate DESC", Event.class);
        List<Event> events = query.getResultList();
        return events;
    }
    //TODO; paging, refactoring to QueryDSL
    public List<Event> findAllByKeyword(String keyword){
        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e" + "WHERE e.title LIKE CONCAT('%', :keyword, '%')" + "OR e.content LIKE CONCAT('%', :keyword, '%')", Event.class);
        query.setParameter("keyword", keyword);
        List<Event> events = query.getResultList();
        return events;
    }
    //TODO; add findAllByDistance method

    //TODO; refactoring to QueryDSL

    public Event findOneById(Long eventId){
        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e WHERE e.id=:eventId", Event.class);
        query.setParameter("eventId", eventId);
        Event event = query.getSingleResult();
        return event;
    }
}

