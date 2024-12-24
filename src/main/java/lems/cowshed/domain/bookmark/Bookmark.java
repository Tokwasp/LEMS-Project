package lems.cowshed.domain.bookmark;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.bookmarkevent.BookmarkEvent;
import lems.cowshed.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @OneToMany(mappedBy = "bookmark")
    private List<BookmarkEvent> bookmarkEvent;

    @Builder
    private Bookmark(String name, User user) {
        this.name = name;
        this.user = user;
    }

    //연관관계 메서드
    public void setUser(User user){
        this.user = user;
        user.addBookmark(List.of(this));
    }

    public void editName(String newBookmarkFolderName) {
        if(newBookmarkFolderName != null){
            name = newBookmarkFolderName;
        }
    }
}