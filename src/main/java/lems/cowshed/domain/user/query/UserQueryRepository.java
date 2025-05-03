package lems.cowshed.domain.user.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lems.cowshed.api.controller.dto.user.response.query.EventParticipantQueryDto;
import lems.cowshed.api.controller.dto.user.response.query.QEventParticipantQueryDto;
import org.springframework.stereotype.Repository;

import java.util.*;

import static lems.cowshed.domain.event.participation.QEventParticipant.*;
import static lems.cowshed.domain.user.QUser.*;

@Repository
public class UserQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public UserQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<EventParticipantQueryDto> getEventParticipants(Long eventId) {
        return queryFactory
                .select(new QEventParticipantQueryDto(
                        user.username.as("name"),
                        user.mbti
                ))
                .from(eventParticipant)
                .join(eventParticipant.user, user)
                .on(eventParticipant.event.id.eq(eventId))
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