package lems.cowshed.domain.user;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Entity
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 45, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    private String email;

    @Column(name = "birth")
    private LocalDateTime birth;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "characters", length = 45)
    private String character;

    @Column(name = "introduction", length = 200)
    private String introduction;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

   /* @OneToMany(mappedBy = "userId")
    private List<Bookmark> bookmarks;*/

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