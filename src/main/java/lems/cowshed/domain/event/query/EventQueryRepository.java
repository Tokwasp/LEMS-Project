package lems.cowshed.domain.event.query;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lems.cowshed.api.controller.dto.event.response.EventDetailResponseDto;
import lems.cowshed.api.controller.dto.event.response.QEventDetailResponseDto;
import org.springframework.stereotype.Repository;

import static lems.cowshed.domain.event.QEvent.*;
import static lems.cowshed.domain.userevent.QUserEvent.*;

@Repository
public class EventQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public EventQueryRepository(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public EventDetailResponseDto getEventWithParticipated(Long eventId){
        return queryFactory
                .select(new QEventDetailResponseDto(
                        event.id,
                        event.name,
                        event.author,
                        event.category,
                        event.createdDateTime,
                        event.location,
                        event.content,
                        event.eventDate,
                        event.capacity,
                        userEvent.event.id.count(),
                        Expressions.stringTemplate("GROUP_CONCAT({0})", userEvent.user.id)
                )).from(userEvent)
                .rightJoin(userEvent.event, event)
                .on(userEvent.event.id.eq(event.id))
                .where(event.id.eq(eventId))
                .groupBy(event.id)
                .fetchOne();
    }
}