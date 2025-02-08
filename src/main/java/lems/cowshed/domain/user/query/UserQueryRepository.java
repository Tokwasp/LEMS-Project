package lems.cowshed.domain.user.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lems.cowshed.api.controller.dto.event.response.EventPreviewResponseDto;
import lems.cowshed.api.controller.dto.event.response.QEventPreviewResponseDto;
import lems.cowshed.api.controller.dto.user.response.UserMyPageResponseDto;
import org.springframework.stereotype.Repository;

import java.util.List;

import static lems.cowshed.domain.bookmark.QBookmark.*;
import static lems.cowshed.domain.event.QEvent.*;
import static lems.cowshed.domain.user.QUser.*;
import static lems.cowshed.domain.userevent.QUserEvent.*;

@Repository
public class UserQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

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

    public UserMyPageResponseDto findUserForMyPage(Long userId) {
        final int LIMIT_COUNT = 5;

        UserMyPageQueryDto userDto = queryFactory
                .select(new QUserMyPageQueryDto(
                        user.username.as("name"),
                        user.birth,
                        user.mbti
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();

        List<UserEventMyPageQueryDto> userEventDto = queryFactory
                .select(new QUserEventMyPageQueryDto(
                        event.id,
                        event.author,
                        event.name.as("eventName"),
                        event.eventDate
                ))
                .from(userEvent)
                .join(userEvent.event, event)
                .where(user.id.eq(userId))
                .limit(LIMIT_COUNT)
                .fetch();

        List<EventPreviewResponseDto> bookmarks = queryFactory
                .select(new QEventPreviewResponseDto(
                                event.id.as("eventId"),
                                event.name,
                                event.author,
                                event.content,
                                event.eventDate,
                                event.capacity,
                                event.applicants,
                                event.createdDateTime
                        )
                )
                .from(bookmark)
                .join(bookmark.event, event)
                .where(user.id.eq(userId))
                .limit(LIMIT_COUNT)
                .fetch();

        return new UserMyPageResponseDto(userDto, userEventDto, bookmarks);
    }

}