package lems.cowshed.domain.post;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.comment.Comment;
import lems.cowshed.domain.event.Event;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
public class Post extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Long userId;

    private String subject;
    private String content;

    protected Post() {}

    @Builder
    private Post(Event event, Long userId, String subject, String content) {
        this.event = event;
        this.userId = userId;
        this.subject = subject;
        this.content = content;
    }

    public void modify(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    public boolean isSameUserId(Long userId){
        return this.userId.equals(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
