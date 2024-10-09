package lems.cowshed.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String password;
    private String role;

    protected User() {}

    public static User createUser(String name, String password, String role) {
        User user = new User();
        user.name = name;
        user.password = password;
        user.role = role;

        return user;
    }
}
