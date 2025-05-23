package lems.cowshed.domain.event.query;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lems.cowshed.api.controller.dto.event.response.*;
import lems.cowshed.dto.event.response.EventSimpleInfo;
import lems.cowshed.dto.event.response.query.EventParticipantQueryDto;
import lems.cowshed.api.controller.dto.event.response.query.QEventParticipantQueryDto;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.regular.event.RegularEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static lems.cowshed.domain.bookmark.BookmarkStatus.BOOKMARK;
import static lems.cowshed.domain.bookmark.QBookmark.bookmark;
import static lems.cowshed.domain.event.QEvent.*;
import static lems.cowshed.domain.event.participation.QEventParticipation.*;
import static lems.cowshed.domain.regular.event.QRegularEvent.*;
import static lems.cowshed.domain.regular.event.participation.QRegularEventParticipation.*;
import static lems.cowshed.domain.user.QUser.user;

@Repository
public class EventQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public EventQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<EventParticipantQueryDto> getEventParticipants(Long eventId) {
        return queryFactory
                .select(new QEventParticipantQueryDto(
                        user.username.as("name"),
                        user.mbti
                ))
                .from(eventParticipation)
                .join(eventParticipation.user, user)
                .on(eventParticipation.event.id.eq(eventId))
                .fetch();
    }

    public Event findEventFetchParticipants(Long eventId){
        return queryFactory
                .select(event)
                .from(event)
                .leftJoin(event.participants, eventParticipation).fetchJoin()
                .where(event.id.eq(eventId))
                .fetchOne();
    }

    public List<RegularEvent> findRegularEventsFetchParticipants(Long eventId) {
        return queryFactory
                .select(regularEvent)
                .from(regularEvent)
                .join(regularEvent.event, event)
                .leftJoin(regularEvent.participations, regularEventParticipation).fetchJoin()
                .where(regularEvent.event.id.eq(eventId))
                .fetch();
    }

    public List<ParticipatingEventSimpleInfoQuery> findEventsParticipatedByUserWithApplicants(List<Long> eventIds) {
        // 회원이 참여한 모임과 참여 인원수 북마크 여부 X
        return queryFactory
                .select(new QParticipatingEventSimpleInfoQuery(
                        event.id,
                        event.name,
                        event.author,
                        event.content,
                        eventParticipation.user.id.countDistinct().as("applicants"),
                        event.capacity,
                        event.createdDateTime
                ))
                .from(eventParticipation)
                .rightJoin(eventParticipation.event, event)
                .where(eventParticipation.event.id.in(eventIds))
                .groupBy(event.id)
                .fetch();
    }

    public List<BookmarkedEventSimpleInfoQuery> findBookmarkedEventsFromUser(Long userId, Pageable pageable) {
        // 북마크 여부 O 참여 인원수 X
        return queryFactory
                .select(new QBookmarkedEventSimpleInfoQuery(
                                event.id,
                                event.name,
                                event.author,
                                event.content,
                                event.capacity,
                                event.createdDateTime,
                                bookmark.status
                        )
                )
                .from(bookmark)
                .join(bookmark.event, event)
                .on(bookmark.event.id.eq(event.id)
                        .and(bookmark.user.id.eq(userId))
                        .and(bookmark.status.eq(BOOKMARK)))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }


    public List<Long> getEventIdsParticipatedByUser(Long userId, Pageable pageable) {
        return queryFactory
                .select(event.id)
                .from(eventParticipation)
                .where(eventParticipation.user.id.eq(userId))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }

    public List<Long> getBookmarkedEventIdSet(Long userId, List<Long> eventIds) {
        return queryFactory.select(bookmark.event.id)
                .from(bookmark)
                .where(user.id.eq(userId)
                        .and(bookmark.event.id.in(eventIds)).and(bookmark.status.eq(BOOKMARK)))
                .fetch();
    }

    public Map<Long,Long> findEventParticipantCountByEventIds(List<Long> eventIds) {
        List<Tuple> tuples = queryFactory.select(eventParticipation.event.id, eventParticipation.event.id.count())
                .from(eventParticipation)
                .where(eventParticipation.event.id.in(eventIds))
                .groupBy(eventParticipation.event.id)
                .fetch();

        return tuples.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(eventParticipation.event.id),
                        tuple -> Optional.ofNullable(tuple.get(eventParticipation.event.id.count())).orElse(0L)
                ));
    }

    public List<EventSimpleInfo> searchEventsWithBookmarkStatus(String content, Long userId) {
        return queryFactory.select(
                new QEventSimpleInfo(
                        event.id,
                        event.name,
                        event.author,
                        event.content,
                        event.capacity,
                        event.createdDateTime,
                        bookmark.status
                ))
                .from(bookmark)
                .rightJoin(bookmark.event, event)
                .on(bookmark.event.id.eq(event.id)
                        .and(bookmark.user.id.eq(userId))
                        .and(bookmark.status.eq(BOOKMARK)))
                .where(event.name.contains(content).or(event.content.contains(content)))
                .fetch();
    }
}