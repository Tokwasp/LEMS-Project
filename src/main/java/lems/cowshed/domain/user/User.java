package lems.cowshed.domain.user;

import jakarta.persistence.*;
import lems.cowshed.domain.bookmark.Bookmark;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "Users")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 45)
    private String username;

    private String password;

    private String role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String email;

    private LocalDateTime birth;

    @Column(length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private Mbti personality;

    @Column(length = 200)
    private String introduction;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

   @OneToMany(mappedBy = "id")
    private List<Bookmark> bookmarks;

    protected User() {}

    public static User createUser(String email, String username, String password, String role) {
        User user = new User();
        user.email = email;
        user.username = username;
        user.password = password;
        user.role = role;

        return user;
    }
}