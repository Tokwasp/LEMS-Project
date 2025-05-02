package lems.cowshed.domain.bookmark;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lems.cowshed.domain.bookmark.BookmarkStatus.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

    @Id
    @Column(name = "bookmark_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private BookmarkStatus status;

    @Builder
    private Bookmark(Event event, User user, BookmarkStatus status) {
        this.event = event;
        this.user = user;
        this.status = status;
    }

    public static Bookmark create(Event event, User user, BookmarkStatus status){
        return Bookmark.builder()
                .event(event)
                .user(user)
                .status(status)
                .build();
    }

    public void deleteBookmark(){
        this.status = DELETE;
    }
}