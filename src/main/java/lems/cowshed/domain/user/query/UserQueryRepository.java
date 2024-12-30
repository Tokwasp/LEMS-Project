package lems.cowshed.domain.user.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lems.cowshed.api.controller.dto.user.response.UserMyPageResponseDto;
import lems.cowshed.domain.bookmark.QBookmark;
import org.springframework.stereotype.Repository;

import java.util.List;

import static lems.cowshed.domain.bookmark.QBookmark.*;
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

    //TODO 확인이 필요
    public List<UserEventQueryDto> findUserWithEvent(Long userId) {
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
                .where(userEvent.user.id.eq(userId))
                .fetch();
    }

    //TODO event 완성시 이벤트 추가
    public UserMyPageResponseDto findUserForMyPage(Long userId){
        UserMyPageQueryDto userDto = queryFactory
                .select(new QUserMyPageQueryDto(
                        user.username.as("name"),
                        user.birth,
                        user.mbti
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();

        List<UserBookmarkMyPageQueryDto> bookmarks = queryFactory
                .select(new QUserBookmarkMyPageQueryDto(
                        bookmark.id,
                        bookmark.name.as("bookmarkName"),
                        bookmark.modifiedDateTime
                ))
                .from(bookmark)
                .join(bookmark.user, user)
                .where(user.id.eq(userId))
                .fetch();

        return new UserMyPageResponseDto(userDto, null, bookmarks);
    }

}