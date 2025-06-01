package lems.cowshed.domain.user;

import jakarta.persistence.*;

import lems.cowshed.domain.BaseEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Entity
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 45)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String email;

    private LocalDate birth;

    @Column(length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private Mbti mbti;

    @Column(length = 200)
    private String introduction;

    protected User() {}

    @Builder
    private User(Long id, String username, String password,
                 Role role, String email, Mbti mbti,
                 String location, LocalDate birth, Gender gender) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.mbti = mbti;
        this.location = location;
        this.birth = birth;
        this.gender = gender;
        this.role = role;
        this.email = email;
    }

    public static User createUserForDetails(Long id, String username, String password, Role role, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .password(password)
                .role(role)
                .email(email)
                .build();
    }

    public void modify(String username, String introduction, String location, LocalDate birth, Mbti mbti){
        this.username = username;
        this.introduction = introduction;
        this.location = location;
        this.birth = birth;
        this.mbti = mbti;
    }

    public void modifyPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}