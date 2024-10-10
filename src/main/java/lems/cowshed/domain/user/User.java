package lems.cowshed.domain.user;

import jakarta.persistence.*;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.event.Event;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 45, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "email", length = 45, nullable = false)
    private String email;

    @Temporal(TemporalType.DATE)
    @Column(name = "birth", nullable = false)
    private Date birth;

    @Column(name = "location", length = 100, nullable = false)
    private String location;

    @Column(name = "character", length = 45)
    private String character;

    @Column(name = "introduction", length = 200)
    private String introduction;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date", nullable = false)
    private Date lastModifiedDate;

    @OneToMany(mappedBy = "userId")
    private List<Bookmark> bookmarks;

=======
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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
