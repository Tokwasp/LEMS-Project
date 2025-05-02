package lems.cowshed.domain.event.query;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lems.cowshed.api.controller.dto.event.response.*;
import lems.cowshed.domain.event.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static lems.cowshed.domain.bookmark.BookmarkStatus.BOOKMARK;
import static lems.cowshed.domain.bookmark.QBookmark.bookmark;
import static lems.cowshed.domain.event.QEvent.*;
import static lems.cowshed.domain.event.participation.QEventParticipant.*;
import static lems.cowshed.domain.user.QUser.user;

@Repository
public class EventQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public EventQueryRepository(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Event findEventWithParticipants(Long eventId){
        return queryFactory
                .select(event)
                .from(event)
                .leftJoin(event.participants, eventParticipant).fetchJoin()
                .where(event.id.eq(eventId))
                .fetchOne();
    }

    public List<Long> findParticipantUserIds(Long eventId){
        return queryFactory
                .select(eventParticipant.user.id)
                .from(eventParticipant)
                .join(eventParticipant.event, event)
                .where(event.id.eq(eventId))
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
                        eventParticipant.user.id.countDistinct().as("applicants"),
                        event.capacity,
                        event.createdDateTime
                ))
                .from(eventParticipant)
                .rightJoin(eventParticipant.event, event)
                .where(eventParticipant.event.id.in(eventIds))
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
                .from(eventParticipant)
                .where(eventParticipant.user.id.eq(userId))
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
        List<Tuple> tuples = queryFactory.select(eventParticipant.event.id, eventParticipant.event.id.count())
                .from(eventParticipant)
                .where(eventParticipant.event.id.in(eventIds))
                .groupBy(eventParticipant.event.id)
                .fetch();

        return tuples.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(eventParticipant.event.id),
                        tuple -> Optional.ofNullable(tuple.get(eventParticipant.event.id.count())).orElse(0L)
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