package lems.cowshed.domain.bookmark;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.event.Event;
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

    private Long userId;

    @Enumerated(EnumType.STRING)
    private BookmarkStatus status;

    @Builder
    private Bookmark(Long userId, BookmarkStatus status) {
        this.userId = userId;
        this.status = status;
    }

    public static Bookmark of(Long userId){
        return Bookmark.builder()
                .userId(userId)
                .status(BOOKMARK)
                .build();
    }

    public void connectEvent(Event event){
        this.event = event;
        event.getBookmarks().add(this);
    }

}