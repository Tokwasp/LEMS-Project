package lems.cowshed.domain.user.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import static lems.cowshed.domain.user.QUser.*;

@Repository
public class UserQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public UserQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
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