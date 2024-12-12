package lems.cowshed.domain.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
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

    /**
     *lastEventId==null이면 처음 조회하는 것이므로 where절에 null을 주고 desc로 정렬된 id 중 첫 번째 event부터 조회한다.
     * lastEventId!=null이면 eventId 중 lastEventId보다 작은 값부터 조회한다.
     * (QueryDsl의 .lt() less than을 사용하여 'where event.id < lastEventId' 생성)
     */
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
}
