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
                        user.mbti,
                        user.gender
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();
    }
}