package lems.cowshed.domain.user;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    private final String TEMP_PASSWORD = "1234";

    @Autowired
    EntityManager em;
    @Autowired
    UserRepository userRepository;

    @Test
    public void basicTest(){
        User user1 = createUser("test1", "test2@naver.com", TEMP_PASSWORD);
        userRepository.save(user1);

        em.flush();
        em.clear();

        User user = userRepository.findByName(user1.getUsername()).get();
        assertThat(user.getUsername()).isEqualTo(user1.getUsername());

    }

    private User createUser(String username, String mail, String password) {
        return User.builder()
                .username(username)
                .email(mail)
                .password(password)
                .build();
    }
}