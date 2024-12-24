package lems.cowshed.domain.bookmarkevent;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.bookmark.Bookmark;
import lems.cowshed.domain.event.Event;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
public class BookmarkEvent extends BaseEntity {
    @Id
    @Column(name = "bookmark_event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "bookmark_id")
    private Bookmark bookmark;
}

