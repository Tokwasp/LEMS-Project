
//package lems.cowshed.domain.user;
//
//
//
//import java.util.List;
//import java.util.Optional;
//
//public interface UserRepository {
//    User save(User user);
//    Optional<User> findById(Long userId);
//    List<User> findEventUsers(Long eventId);
//    User edit();
//}

package lems.cowshed.domain.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static lems.cowshed.domain.user.QUser.*;

@Repository
public class UserRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public UserRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public void save(User user){
        em.persist(user);
    }

    public Optional<User> findById(Long id){
        User findMember = em.find(User.class, id);
        return Optional.ofNullable(findMember);
    }

    public Optional<User> findByEmail(String email){
        return Optional.ofNullable(queryFactory
                .selectFrom(user)
                .where(user.email.eq(email))
                .fetchOne()
        );
    }

    public Optional<User> findByName(String username) {
        return Optional.ofNullable(queryFactory
                        .selectFrom(user)
                        .where(user.username.eq(username))
                        .fetchOne()
        );
    }
}
