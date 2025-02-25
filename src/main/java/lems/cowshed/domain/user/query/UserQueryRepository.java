package lems.cowshed.domain.user.query;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.api.controller.dto.event.response.QEventPreviewResponseDto;
import lems.cowshed.api.controller.dto.user.response.UserMyPageResponseDto;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<UserEventQueryDto> findUserParticipatingInEvent (Long userId) {
        return queryFactory
                .select(new QUserEventQueryDto(
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

    public UserMyPageResponseDto findUserForMyPage(Long userId, List<Long> eventIdList) {

        UserMyPageQueryDto userDto = queryFactory
                .select(new QUserMyPageQueryDto(
                        user.username.as("name"),
                        user.birth,
                        user.mbti
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();

        // 회원이 참여한 모임과 참여 인원수 북마크 여부 X
        List<UserEventMyPageQueryDto> userEventDto = queryFactory
                .select(new QUserEventMyPageQueryDto(
                        event.id,
                        event.author,
                        event.name.as("eventName"),
                        event.eventDate,
                        userEvent.user.id.countDistinct().as("applicants")
                ))
                .from(userEvent)
                .join(userEvent.event, event)
                .where(userEvent.event.id.in(eventIdList))
                .groupBy(event.id)
                .fetch();

        // 북마크 여부 O 참여자 수 체크 X
        List<EventPreviewResponseDto> bookmarks = queryFactory
                .select(new QEventPreviewResponseDto(
                                event.id.as("eventId"),
                                event.author,
                                event.name,
                                event.eventDate,
                                bookmark.status
                        )
                )
                .from(bookmark)
                .join(bookmark.event, event)
                .where(bookmark.user.id.eq(userId)
                        .and(bookmark.status.eq(BOOKMARK)))
                .limit(LIMIT_COUNT)
                .fetch();

        return new UserMyPageResponseDto(userDto, userEventDto, bookmarks);
    }

    public List<Long> getParticipatedEvent(Long userId){
        return queryFactory
                .select(event.id)
                .from(userEvent)
                .where(userEvent.user.id.eq(userId))
                .limit(LIMIT_COUNT)
                .fetch();
    }

    public Set<Long> getBookmark(Long userId, List<Long> eventIds){
        return new HashSet<>(queryFactory.select(bookmark.event.id)
                .from(bookmark)
                .where(user.id.eq(userId)
                        .and(bookmark.event.id.in(eventIds)).and(bookmark.status.eq(BOOKMARK)))
                .fetch());
    }

    public Map<Long, Long> getParticipatedEventIdSet(List<Long> eventIds){
        List<Tuple> result = queryFactory.select(userEvent.event.id, userEvent.event.id.count())
                .from(userEvent)
                .where(userEvent.event.id.in(eventIds))
                .groupBy(userEvent.event.id)
                .fetch();

        Map<Long, Long> eventCountMap = new HashMap<>();
        for(Tuple tuple : result){
            Long eventId = tuple.get(userEvent.event.id);
            Long participantCount = tuple.get(userEvent.event.id.count());

            eventCountMap.put(eventId, participantCount != null ? participantCount : 0);
        }
        return eventCountMap;
    }
}