
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

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public void save(User user){
        em.persist(user);
    }

    public Optional<User> findByEmail(String email){
        return em.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream().findAny();
    }

    public Optional<User> findByName(String username){
        return em.createQuery("select u from User u where u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream().findAny();
    }
}
