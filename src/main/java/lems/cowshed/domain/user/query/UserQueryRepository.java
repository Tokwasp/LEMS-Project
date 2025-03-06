package lems.cowshed.domain.user.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.*;

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

    // 특정 모임에 참여한 회원들 정보
    public List<EventParticipantQueryDto> findParticipants (Long eventId) {
        return queryFactory
                .select(new QEventParticipantQueryDto(
                        user.username.as("name"),
                        user.gender
                ))
                .from(userEvent)
                .join(userEvent.user, user)
                .on(userEvent.event.id.eq(eventId))
                .fetch();
    }

    public MyPageUserQueryDto findUser(Long userId){
        return queryFactory
                .select(new QMyPageUserQueryDto(
                        user.username.as("name"),
                        user.birth,
                        user.mbti,
                        user.gender,
                        user.location,
                        user.introduction
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();
    }
}