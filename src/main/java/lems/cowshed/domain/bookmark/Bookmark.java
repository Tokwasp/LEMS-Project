package lems.cowshed.domain.bookmark;

import jakarta.persistence.*;
import lems.cowshed.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
public class Bookmark {
    @Id
    @Column(name = "bookmark_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "created_date", nullable = false)
    private String createdDate;

    @Column(name = "last_modified_date", nullable = false)
    private String lastModifiedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
