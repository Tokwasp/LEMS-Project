
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

    public Optional<User> findByName(String name){
        return em.createQuery("select u from User u where u.name = :name", User.class)
                .setParameter("name", name)
                .getResultStream().findAny();
    }
}
