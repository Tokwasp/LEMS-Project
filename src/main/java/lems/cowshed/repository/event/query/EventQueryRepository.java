package lems.cowshed.repository.event.query;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lems.cowshed.domain.event.Category;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.regular.event.RegularEvent;
import lems.cowshed.dto.event.response.query.EventParticipantQueryDto;
import lems.cowshed.dto.event.response.query.QEventParticipantQueryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static lems.cowshed.domain.bookmark.BookmarkStatus.BOOKMARK;
import static lems.cowshed.domain.bookmark.QBookmark.bookmark;
import static lems.cowshed.domain.event.QEvent.event;
import static lems.cowshed.domain.event.participation.QEventParticipation.eventParticipation;
import static lems.cowshed.domain.regular.event.QRegularEvent.regularEvent;
import static lems.cowshed.domain.regular.event.participation.QRegularEventParticipation.regularEventParticipation;
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
                .on(eventParticipation.eventId.eq(eventId))
                .fetch();
    }

    public List<RegularEvent> findRegularEventsFetchParticipants(Long eventId) {
        return queryFactory
                .select(regularEvent).distinct()
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
                .rightJoin(event).on(eventParticipation.eventId.eq(event.id))
                .where(eventParticipation.eventId.in(eventIds))
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
                        .and(bookmark.userId.eq(userId))
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
        List<Tuple> tuples = queryFactory.select(eventParticipation.eventId, eventParticipation.eventId.count())
                .from(eventParticipation)
                .where(eventParticipation.eventId.in(eventIds))
                .groupBy(eventParticipation.eventId)
                .fetch();

        return tuples.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(eventParticipation.eventId),
                        tuple -> Optional.ofNullable(tuple.get(eventParticipation.eventId.count())).orElse(0L)
                ));
    }

    public Slice<Event> search(Pageable pageable, String keyword, Category category) {
        List<Event> events = queryFactory.selectFrom(event)
                .where(keywordLike(keyword), categoryIn(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(event.createdDateTime.desc())
                .fetch();

        boolean hasNext = events.size() > pageable.getPageSize();

        if (hasNext) {
            events.remove(events.size() - 1);
        }

        return new SliceImpl<>(events, pageable, hasNext);
    }

    public int searchCount(String content, Category category) {
        Long count = queryFactory.select(event.count())
                .from(event)
                .where(keywordLike(content), categoryIn(category))
                .fetchOne();

        return count.intValue();
    }

    public List<Event> findEventFetchParticipantsIn(List<Long> eventIds) {
        return queryFactory.selectFrom(event)
                .leftJoin(event.participants, eventParticipation).fetchJoin()
                .where(event.id.in(eventIds))
                .fetch();
    }

    private BooleanBuilder categoryIn(Category category) {
        return nullSafeBuilder(() -> event.category.in(category));
    }

    private BooleanBuilder keywordLike(String content) {
        return nullSafeBuilder(() -> event.content.contains(content).or(event.name.contains(content)));
    }

    private BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f){
        try{
            return new BooleanBuilder(f.get());
        } catch (Exception e){
            return new BooleanBuilder();
        }
    }
}