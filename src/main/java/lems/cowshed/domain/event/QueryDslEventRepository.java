package lems.cowshed.domain.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
                        event.createdDate
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
    public List<EventPreviewResponseDto> findAllByCategory(String category){
        TypedQuery<EventPreviewResponseDto> query = em.createQuery("SELECT new lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto(e.id, e.name, e.content, e.eventDate, e.capacity, e.applicants, e.createdDate)" + "FROM Event e" + "WHERE category=:category", EventPreviewResponseDto.class);
        query.setParameter("category", category);
        List<EventPreviewResponseDto> eventPreviewResponseDtos = query.getResultList();
        return eventPreviewResponseDtos;
    }

    //TODO; paging, refactoring to QueryDSL
    public List<EventPreviewResponseDto> findAllOrderByApplicants(){
        TypedQuery<EventPreviewResponseDto> query = em.createQuery("SELECT new lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto(e.id, e.name, e.content, e.eventDate, e.capacity, e.applicants, e.createdDate)" + "FROM Event e" + "ORDER BY e.applicants ASC", EventPreviewResponseDto.class);
        List<EventPreviewResponseDto> eventPreviewResponseDtos = query.getResultList();
        return eventPreviewResponseDtos;
    }

    //TODO; paging, refactoring to QueryDSL
    public List<EventPreviewResponseDto> findAllOrderByCreatedDate(){
        TypedQuery<EventPreviewResponseDto> query = em.createQuery("SELECT new lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto(e.id, e.name, e.content, e.eventDate, e.capacity, e.applicants, e.createdDate)" + "FROM Event e" + "ORDER BY e.createdDate DESC", EventPreviewResponseDto.class);
        List<EventPreviewResponseDto> eventPreviewResponseDtos = query.getResultList();
        return eventPreviewResponseDtos;
    }
    //TODO; paging, refactoring to QueryDSL
    public List<EventPreviewResponseDto> findAllByKeyword(String keyword){
        TypedQuery<EventPreviewResponseDto> query = em.createQuery("SELECT new lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto(e.id, e.name, e.content, e.eventDate, e.capacity, e.applicants, e.createdDate)" + "FROM Event e" + "WHERE e.title LIKE CONCAT('%', :keyword, '%')" + "OR e.content LIKE CONCAT('%', :keyword, '%')", EventPreviewResponseDto.class);
        query.setParameter("keyword", keyword);
        List<EventPreviewResponseDto> eventPreviewResponseDtos = query.getResultList();
        return eventPreviewResponseDtos;
    }
    //TODO; add findAllByDistance method
}

