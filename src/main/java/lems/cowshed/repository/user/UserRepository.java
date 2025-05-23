package lems.cowshed.repository.user;

import lems.cowshed.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmailOrUsername(String email, String username);
    List<User> findByIdIn(List<Long> ids);
}
