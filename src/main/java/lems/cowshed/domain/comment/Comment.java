package lems.cowshed.domain.comment;

import jakarta.persistence.*;
import lems.cowshed.domain.BaseEntity;
import lems.cowshed.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private Long userId;
    private String context;

    protected Comment() {}

    @Builder
    private Comment(Long userId, String context) {
        this.userId = userId;
        this.context = context;
    }

    public void connectPost(Post post){
        this.post = post;
        post.getComments().add(this);
    }

    public void modify(String comment) {
        this.context = comment;
    }

    public boolean isMyUserId(Long userId) {
        return this.userId.equals(userId);
    }
}
