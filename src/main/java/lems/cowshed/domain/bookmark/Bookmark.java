package lems.cowshed.domain.bookmark;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.bookmarkevent.BookmarkEvent;
import lems.cowshed.domain.event.Event;
import lems.cowshed.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

    @Id
    @Column(name = "bookmark_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "bookmark" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookmarkEvent> bookmarkEvent = new ArrayList<>();

    @Builder
    private Bookmark(String name, User user) {
        this.user = user;
        this.name = name;
    }

    public static Bookmark create(String name, User user){
        Bookmark bookmark = Bookmark.builder()
                .name(name)
                .user(user)
                .build();
        user.getBookmarks().add(bookmark);
        return bookmark;
    }

    public void editName(String newBookmarkFolderName) {
        if(newBookmarkFolderName != null){
            name = newBookmarkFolderName;
        }
    }
}