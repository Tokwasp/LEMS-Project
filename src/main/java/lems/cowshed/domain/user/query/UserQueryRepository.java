package lems.cowshed.domain.user.query;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lems.cowshed.domain.event.query.MyPageBookmarkedEventQueryDto;
import lems.cowshed.domain.event.query.MyPageParticipatingEventQueryDto;
import lems.cowshed.domain.event.query.QMyPageBookmarkedEventQueryDto;
import lems.cowshed.domain.event.query.QMyPageParticipatingEventQueryDto;
import org.springframework.stereotype.Repository;

import java.util.*;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;
import static lems.cowshed.domain.bookmark.QBookmark.*;
import static lems.cowshed.domain.event.QEvent.*;
import static lems.cowshed.domain.user.QUser.*;
import static lems.cowshed.domain.userevent.QUserEvent.*;

@Repository
public class UserQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    private static final int LIMIT_COUNT = 5;

    public UserQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 모임에 참가한 회원 정보
    public List<EventParticipantQueryDto> findUserParticipatingInEvent (Long userId) {
        return queryFactory
                .select(new QEventParticipantQueryDto(
                        user.username,
                        user.gender,
                        user.mbti,
                        user.birth,
                        user.location
                ))
                .from(userEvent)
                .join(userEvent.user, user)
                .where(user.id.eq(userId))
                .fetch();
    }

    public MyPageUserQueryDto findUser(Long userId){
        return queryFactory
                .select(new QMyPageUserQueryDto(
                        user.username.as("name"),
                        user.birth,
                        user.mbti
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();
    }

    public List<MyPageParticipatingEventQueryDto> findParticipatedEvents(List<Long> eventIds) {
        // 회원이 참여한 모임과 참여 인원수 북마크 여부 X
        return queryFactory
                .select(new QMyPageParticipatingEventQueryDto(
                        event.id,
                        event.author,
                        event.name.as("eventName"),
                        event.eventDate,
                        userEvent.user.id.countDistinct().as("applicants")
                ))
                .from(userEvent)
                .rightJoin(userEvent.event, event)
                .where(userEvent.event.id.in(eventIds))
                .groupBy(event.id)
                .fetch();
    }

    public List<MyPageBookmarkedEventQueryDto> findBookmarkedEvents(Long userId) {
        // 북마크 여부 O 참여 인원수 X
        return queryFactory
                .select(new QMyPageBookmarkedEventQueryDto(
                                event.id.as("eventId"),
                                event.author,
                                event.name,
                                event.eventDate,
                                bookmark.status
                        )
                )
                .from(bookmark)
                .join(bookmark.event, event)
                .on(bookmark.event.id.eq(event.id)
                        .and(bookmark.user.id.eq(userId))
                        .and(bookmark.status.eq(BOOKMARK)))
                .limit(LIMIT_COUNT)
                .fetch();
    }


    public List<Long> getParticipatedEventsId(Long userId){
        return queryFactory
                .select(event.id)
                .from(userEvent)
                .where(userEvent.user.id.eq(userId))
                .limit(LIMIT_COUNT)
                .fetch();
    }

    public List<Long> getBookmarkedEventIdSet(Long userId, List<Long> eventIds){
        return queryFactory.select(bookmark.event.id)
                .from(bookmark)
                .where(user.id.eq(userId)
                        .and(bookmark.event.id.in(eventIds)).and(bookmark.status.eq(BOOKMARK)))
                .fetch();
    }

    public List<Tuple> findEventIdParticipants(List<Long> eventIds){
        return queryFactory.select(userEvent.event.id, userEvent.event.id.count())
                .from(userEvent)
                .where(userEvent.event.id.in(eventIds))
                .groupBy(userEvent.event.id)
                .fetch();
    }
}